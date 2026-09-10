(ns cultural.governor
  "Instruction Integrity Governor -- the independent compliance layer
  that earns the StudioEdOps-LLM the right to commit. The LLM has no
  notion of jurisdictional cultural-education/child-performer law,
  whether a student's own recorded child-performer work permit has
  actually stayed unresolved, whether a student's own recorded
  practice hours actually satisfy their own recorded requirement, or
  when an act stops being a draft and becomes a real-world
  certification/progress-record finalization, so this MUST be a
  separate system able to *reject* a proposal and fall back to HOLD
  -- the cultural-education analog of `cloud-itonami-isic-8620`'s
  ClinicGovernor.

  Five checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them (you don't get to approve your way
  past a fabricated jurisdiction spec-basis, incomplete evidence, an
  unresolved child-performer work permit, insufficient practice
  hours, or a double finalization). The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `cultural.phase`: for `:stake
  :actuation/finalize-certification` (a real certification/progress-
  record finalization) NO phase ever allows auto-commit either. Two
  independent layers agree that actuation is always a human call.

    1. Spec-basis                  -- did the curriculum proposal cite
                                       an OFFICIAL source (`cultural.
                                       facts`), or invent one?
    2. Evidence incomplete         -- for `:actuation/finalize-
                                       certification`, has the student
                                       actually been assessed with a
                                       full student-enrollment-
                                       consent-record/curriculum-
                                       record/child-performer-permit-
                                       verification-record/
                                       certification-completion-
                                       record evidence checklist on
                                       file?
    3. Child-performer work permit
       unresolved                    -- reported by THIS proposal
                                       itself (a `:permit/screen` that
                                       just found an unresolved child-
                                       performer permit), or already on
                                       file for the student
                                       (`:permit/screen`/`:actuation/
                                       finalize-certification`).
                                       Evaluated UNCONDITIONALLY (not
                                       scoped to a specific op) so the
                                       screening op itself can HARD-
                                       hold on its own finding. A
                                       GENUINELY NEW concept in this
                                       fleet (grep-verified absent --
                                       no dedicated child-performer
                                       work-permit CHECK FUNCTION
                                       exists anywhere else in this
                                       fleet), the 53rd distinct
                                       application of the
                                       unconditional-evaluation
                                       discipline overall (`casualty.
                                       governor/sanctions-violations`'s
                                       original fix; most recently
                                       `residential.governor/
                                       background-check-not-cleared-
                                       violations` at 52nd). Grounded
                                       in real child-performer labor
                                       law (California Labor Code
                                       section 1308.5, UK's Children
                                       (Performances and Activities)
                                       Regulations 2014, Japan's Labor
                                       Standards Act Article 56/57,
                                       Germany's JArbSchG section 6).
    4. Practice hours insufficient  -- for `:actuation/finalize-
                                       certification`, INDEPENDENTLY
                                       recompute whether the student's
                                       own recorded completed practice
                                       hours fall short of their own
                                       recorded required practice
                                       hours (`cultural.registry/
                                       practice-hours-insufficient?`)
                                       -- needs no proposal inspection
                                       at all. An HONEST reuse of this
                                       fleet's MINIMUM-threshold
                                       sufficiency check family (the
                                       NINTH instance), directly
                                       analogous to `secondary.
                                       registry/attendance-hours-
                                       insufficient?`'s own hours-
                                       completed/hours-required shape,
                                       not claimed as new.
    5. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:actuation/
                                       finalize-certification` (a REAL
                                       certification/progress-record
                                       finalization) -> escalate.

  One more guard, double-finalization prevention, is enforced but NOT
  listed as a numbered HARD check above because it needs no upstream
  comparison at all -- `already-finalized-violations` refuses to
  finalize a certification for the SAME student twice, off a
  dedicated `:certification-finalized?` fact (never a `:status`
  value) -- the SAME 'check a dedicated boolean, not status'
  discipline every prior sibling governor's guards establish, informed
  by `cloud-itonami-isic-6492`'s status-lifecycle bug
  (ADR-2607071320)."
  (:require [cultural.facts :as facts]
            [cultural.registry :as registry]
            [cultural.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Finalizing a real certification or progress record is the ONE
  real-world actuation event this actor performs -- a single-member
  set, matching `cloud-itonami-isic-6511`'s/`6621`'s/`6629`'s/`6612`'s/
  `6492`'s/`7120`'s/`8620`'s/`edsupport`'s single-actuation shape."
  #{:actuation/finalize-certification})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:curriculum/verify` (or `:actuation/finalize-certification`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's cultural-education/child-performer
  requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:curriculum/verify :actuation/finalize-certification} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は認定基準として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:actuation/finalize-certification`, the jurisdiction's required
  student-enrollment-consent-record/curriculum-record/child-
  performer-permit-verification-record/certification-completion-
  record evidence must actually be satisfied -- do not trust the
  advisor's self-reported confidence alone."
  [{:keys [op subject]} st]
  (when (= op :actuation/finalize-certification)
    (let [s (store/student st subject)
          curriculum (store/curriculum-of st subject)]
      (when-not (and curriculum
                     (facts/required-evidence-satisfied?
                      (:jurisdiction s) (:checklist curriculum)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(受講同意記録/カリキュラム記録/児童演芸許可確認記録/修了認定記録等)が充足していない状態での提案"}]))))

(defn- child-performer-work-permit-unresolved-violations
  "An unresolved child-performer work permit -- reported by THIS
  proposal (e.g. a `:permit/screen` that itself just found an
  unresolved permit), or already on file in the store for the student
  (`:permit/screen`/`:actuation/finalize-certification`) -- is a
  HARD, un-overridable hold. Evaluated UNCONDITIONALLY (not scoped to
  a specific op) so the screening op itself can HARD-hold on its own
  finding."
  [{:keys [op subject]} proposal st]
  (let [hit-in-proposal? (true? (get-in proposal [:value :child-performer-work-permit-unresolved?]))
        student-id (when (contains? #{:permit/screen :actuation/finalize-certification} op) subject)
        hit-on-file? (and student-id (true? (:child-performer-work-permit-unresolved? (store/student st student-id))))]
    (when (or hit-in-proposal? hit-on-file?)
      [{:rule :child-performer-work-permit-unresolved
        :detail "児童演芸許可が未解決の状態での認定確定提案は進められない"}])))

(defn- practice-hours-insufficient-violations
  "For `:actuation/finalize-certification`, INDEPENDENTLY recompute
  whether the student's own recorded completed practice hours fall
  short of their own recorded required practice hours via `cultural.
  registry/practice-hours-insufficient?` -- needs no proposal
  inspection at all, since its inputs are permanent ground-truth
  fields already on the student."
  [{:keys [op subject]} st]
  (when (= op :actuation/finalize-certification)
    (let [s (store/student st subject)]
      (when (registry/practice-hours-insufficient? s)
        [{:rule :practice-hours-insufficient
          :detail (str subject " の練習時間(" (:practice-hours-completed s)
                      ")が必要時間(" (:practice-hours-required s) ")に満たない")}]))))

(defn- already-finalized-violations
  "For `:actuation/finalize-certification`, refuses to finalize a
  certification for the SAME student twice, off a dedicated
  `:certification-finalized?` fact (never a `:status` value)."
  [{:keys [op subject]} st]
  (when (= op :actuation/finalize-certification)
    (when (store/student-already-finalized? st subject)
      [{:rule :already-finalized
        :detail (str subject " は既に認定確定済み")}])))

(defn check
  "Censors a StudioEdOps-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (child-performer-work-permit-unresolved-violations request proposal st)
                           (practice-hours-insufficient-violations request st)
                           (already-finalized-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
