(ns cultural.culturaladvisor
  "StudioEdOps-LLM client -- the *contained intelligence node* for the
  cultural-education actor.

  It normalizes student intake, drafts a per-jurisdiction cultural-
  education/child-performer evidence checklist, screens students for
  an unresolved child-performer work permit, and drafts the
  certification-finalization action. CRITICAL: it is a smart-but-
  untrusted advisor. It returns a *proposal* (with a rationale + the
  fields it cited), never a committed record or a real certification
  finalization. Every output is censored downstream by `cultural.
  governor` before anything touches the SSoT, and `:actuation/
  finalize-certification` proposals NEVER auto-commit at any phase --
  see README `Actuation`.

  Like every sibling actor's advisor, this is a deterministic mock so
  the actor graph runs offline and the governor contract is exercised
  end-to-end. In production this calls a real LLM (kotoba-llm or
  equivalent) with the same proposal shape.

  Proposal shape (all kinds):
    {:summary    str            ; human-facing draft / finding
     :rationale  str            ; why -- SCANNED by the spec-basis gate
     :cites      [kw|str ..]    ; facts/sources the LLM used -- SCANNED too
     :effect     kw             ; how a commit would mutate the SSoT
     :stake      kw|nil         ; :actuation/finalize-certification | nil
     :confidence 0..1}"
  (:require #?(:clj  [clojure.edn :as edn]
               :cljs [cljs.reader :as edn])
            [clojure.string :as str]
            [cultural.facts :as facts]
            [cultural.registry :as registry]
            [cultural.store :as store]
            [langchain.model :as model]))

(defn- normalize-intake
  "Directory upsert -- the LLM only normalizes/validates the patch; it
  does not invent the student or jurisdiction. High confidence, low
  stakes."
  [_db {:keys [patch]}]
  {:summary    (str "受講者記録更新: " (pr-str (keys patch)))
   :rationale  "入力 patch の正規化のみ。新規事実の生成なし。"
   :cites      (vec (keys patch))
   :effect     :student/upsert
   :value      patch
   :stake      nil
   :confidence 0.97})

(defn- verify-curriculum
  "Per-jurisdiction cultural-education/child-performer evidence
  checklist draft. `:no-spec?` injects the failure mode we must
  defend against: proposing a checklist for a jurisdiction with NO
  official spec-basis in `cultural.facts` -- the Instruction
  Integrity Governor must reject this (never invent a jurisdiction's
  requirements)."
  [db {:keys [subject no-spec?]}]
  (let [s (store/student db subject)
        iso3 (if no-spec? "ATL" (:jurisdiction s))
        sb (facts/spec-basis iso3)]
    (if (nil? sb)
      {:summary    (str iso3 " の公式spec-basisが見つかりません")
       :rationale  "cultural.facts に未登録の法域。要件を推測で作らない。"
       :cites      []
       :effect     :curriculum/set
       :value      {:jurisdiction iso3 :checklist [] :spec-basis nil}
       :stake      nil
       :confidence 0.9}
      {:summary    (str iso3 " (" (:owner-authority sb) ") 向け必要書類 "
                        (count (:required-evidence sb)) " 件を提案")
       :rationale  (str "公式ソース: " (:provenance sb) " / 法的根拠: " (:legal-basis sb))
       :cites      [(:legal-basis sb) (:provenance sb)]
       :effect     :curriculum/set
       :value      {:jurisdiction iso3
                    :checklist (:required-evidence sb)
                    :spec-basis (:provenance sb)
                    :legal-basis (:legal-basis sb)}
       :stake      nil
       :confidence 0.9})))

(defn- screen-permit
  "Child-performer work-permit screening draft.
  `:child-performer-work-permit-unresolved?` on the student record
  injects the failure mode: the Instruction Integrity Governor must
  HOLD, un-overridably, on any unresolved permit."
  [db {:keys [subject]}]
  (let [s (store/student db subject)]
    (cond
      (nil? s)
      {:summary "対象受講者記録が見つかりません" :rationale "no student record"
       :cites [] :effect :permit/set :value {:student-id subject :child-performer-work-permit-unresolved? nil}
       :stake nil :confidence 0.0}

      (true? (:child-performer-work-permit-unresolved? s))
      {:summary    (str (:student-name s) ": 児童演芸許可が未解決")
       :rationale  "スクリーニングが未解決状態を検出。人手確認とホールドが必須。"
       :cites      [:permit-check]
       :effect     :permit/set
       :value      {:student-id subject :child-performer-work-permit-unresolved? true}
       :stake      nil
       :confidence 0.95}

      :else
      {:summary    (str (:student-name s) ": 児童演芸許可は解決済み")
       :rationale  "許可スクリーニング完了。"
       :cites      [:permit-check]
       :effect     :permit/set
       :value      {:student-id subject :child-performer-work-permit-unresolved? false}
       :stake      nil
       :confidence 0.9})))

(defn- propose-certification-finalization
  "Draft the actual CERTIFICATION-FINALIZATION action -- finalizing a
  real certification or progress record. ALWAYS `:stake :actuation/
  finalize-certification` -- this is a REAL-WORLD act, never a draft
  the actor may auto-run. See README `Actuation`: no phase ever adds
  this op to a phase's `:auto` set (`cultural.phase`); the governor
  also always escalates on `:actuation/finalize-certification`. Two
  independent layers agree, deliberately."
  [db {:keys [subject]}]
  (let [s (store/student db subject)
        safe? (and s (not (registry/practice-hours-insufficient? s))
                   (not (:child-performer-work-permit-unresolved? s)))]
    {:summary    (str subject " 向け認定確定提案"
                      (when s (str " (student=" (:student-name s) ")")))
     :rationale  (if s
                   (str "practice-hours-completed=" (:practice-hours-completed s)
                        " practice-hours-required=" (:practice-hours-required s))
                   "受講者記録が見つかりません")
     :cites      (if s [subject] [])
     :effect     :student/mark-finalized
     :value      {:student-id subject}
     :stake      :actuation/finalize-certification
     :confidence (if safe? 0.9 0.3)}))

(defn infer
  "Route a request to the right proposal generator.
  request: {:op kw :subject id ...op-specific...}"
  [db {:keys [op] :as request}]
  (case op
    :student/intake                (normalize-intake db request)
    :curriculum/verify              (verify-curriculum db request)
    :permit/screen                  (screen-permit db request)
    :actuation/finalize-certification (propose-certification-finalization db request)
    {:summary "未対応の操作" :rationale (str op) :cites []
     :effect :noop :stake nil :confidence 0.0}))

;; ----------------------------- Advisor protocol -----------------------------

(defprotocol Advisor
  (-advise [advisor store request] "store + request -> proposal map"))

(defn mock-advisor
  "The deterministic advisor (the `infer` logic above). Default everywhere."
  [] (reify Advisor (-advise [_ st req] (infer st req))))

(def ^:private system-prompt
  (str "あなたは文化教育事業(音楽・美術・舞踊・語学等)の認定確定エージェントの"
       "助言者です。与えられた事実のみに基づき、提案を1つだけEDNマップで"
       "返します。説明や前置きは一切書かず、EDNだけを出力します。\n"
       "キー: :summary(人向けドラフト) :rationale(根拠/必ず事実から) "
       ":cites(使った事実キーのベクタ) "
       ":effect(:student/upsert|:curriculum/set|:permit/set|"
       ":student/mark-finalized) "
       ":stake(:actuation/finalize-certification か nil) :confidence(0..1)。\n"
       "重要: 登録されていない法域の要件を絶対に創作してはいけません。"
       "spec-basisが無い場合は :cites を空にし confidence を上げないこと。"))

(defn- facts-for [st {:keys [op subject]}]
  (case op
    :curriculum/verify                {:student (store/student st subject)}
    :permit/screen                     {:student (store/student st subject)}
    :actuation/finalize-certification  {:student (store/student st subject)}
    {:student (store/student st subject)}))

(defn- parse-proposal
  "Parse the model's EDN proposal defensively. Any parse/shape failure
  yields a safe low-confidence noop so the Instruction Integrity
  Governor escalates/holds -- an LLM hiccup can never auto-finalize a
  certification."
  [content]
  (let [p (try (edn/read-string (str/trim (str content)))
               (catch #?(:clj Exception :cljs :default) _ nil))]
    (if (map? p)
      (-> p
          (update :cites #(vec (or % [])))
          (update :confidence #(if (number? %) (double %) 0.0))
          (update :effect #(or % :noop)))
      {:summary "LLM応答を解釈できませんでした" :rationale (str content)
       :cites [] :effect :noop :stake nil :confidence 0.0})))

(defn llm-advisor
  "An advisor backed by a `langchain.model/ChatModel` (real inference)."
  ([chat-model] (llm-advisor chat-model {}))
  ([chat-model gen-opts]
   (reify Advisor
     (-advise [_ st req]
       (let [msgs [{:role :system :content system-prompt}
                   {:role :user :content (str "操作: " (:op req)
                                              "\n対象: " (:subject req)
                                              "\n事実: " (pr-str (facts-for st req)))}]
             resp (model/-generate chat-model msgs gen-opts)]
         (parse-proposal (:content resp)))))))

(defn trace
  "Decision-grounded audit record -- persisted to the :audit channel."
  [request proposal]
  {:t          :culturaladvisor-proposal
   :op         (:op request)
   :subject    (:subject request)
   :summary    (:summary proposal)
   :rationale  (:rationale proposal)
   :cites      (:cites proposal)
   :confidence (:confidence proposal)})
