(ns cultural.store
  "SSoT for the cultural-education actor, behind a `Store` protocol so
  the backend is a swap, not a rewrite -- the same seam every prior
  `cloud-itonami-isic-*` actor in this fleet uses:

    - `MemStore`     -- atom of EDN. The deterministic default for
                        dev/tests/demo (no deps).
    - `DatomicStore` -- backed by `langchain.db`, a Datomic-API-compatible
                        EAV store (datalog q / pull / upsert). Pure `.cljc`,
                        so it runs offline AND can be pointed at a real
                        Datomic Local or a kotoba-server pod by swapping
                        `langchain.db`'s `:db-api` (see langchain.kotoba-db).

  Both implement the same protocol and pass the same contract
  (test/cultural/store_contract_test.clj), which is the whole point:
  the actor, the Instruction Integrity Governor and the audit ledger
  never know which SSoT they run on.

  Like `clinic.store`'s/`edsupport.store`'s simpler entities, a
  STUDENT is acted on directly by the ONE actuation op -- no
  dynamically-filed sub-record, and the double-finalization guard
  checks a dedicated `:certification-finalized?` boolean rather than a
  `:status` value, the same discipline `clinic.governor`'s/
  `edsupport.governor`'s guards establish.

  NOTE on naming: the protocol's per-entity accessor is `student`
  directly -- not a Clojure special form, so no `-of` suffix
  workaround was needed.

  The ledger stays append-only on every backend: 'which student was
  screened for an unresolved child-performer permit, which
  certification was finalized, on what jurisdictional basis, approved
  by whom' is always a query over an immutable log -- the audit trail
  a family/community trusting a cultural-education operator needs,
  and the evidence an operator needs if a certification decision is
  later disputed."
  (:require #?(:clj  [clojure.edn :as edn]
               :cljs [cljs.reader :as edn])
            [cultural.registry :as registry]
            [langchain.db :as d]))

(defprotocol Store
  (student [s id])
  (all-students [s])
  (permit-screen-of [s student-id] "committed child-performer-permit screening verdict for a student, or nil")
  (curriculum-of [s student-id] "committed curriculum evidence assessment, or nil")
  (ledger [s])
  (certification-history [s] "the append-only certification-finalization history (cultural.registry drafts)")
  (next-sequence [s jurisdiction] "next certification-number sequence for a jurisdiction")
  (student-already-finalized? [s student-id] "has this student's certification already been finalized?")
  (commit-record! [s record] "apply a committed op's record to the SSoT")
  (append-ledger! [s fact]   "append one immutable decision fact")
  (with-students [s students] "replace/seed the student directory (map id->student)"))

;; ----------------------------- demo data -----------------------------

(defn demo-data
  "A small, self-contained student set so the actor + tests run
  offline."
  []
  {:students
   {"student-1" {:id "student-1" :student-name "Sato Kenji"
                :practice-hours-completed 120 :practice-hours-required 100
                :child-performer-work-permit-unresolved? false
                :certification-finalized? false :jurisdiction "JPN" :status :intake}
    "student-2" {:id "student-2" :student-name "Atlantis Doe"
                :practice-hours-completed 120 :practice-hours-required 100
                :child-performer-work-permit-unresolved? false
                :certification-finalized? false :jurisdiction "ATL" :status :intake}
    "student-3" {:id "student-3" :student-name "鈴木花子"
                :practice-hours-completed 50 :practice-hours-required 100
                :child-performer-work-permit-unresolved? false
                :certification-finalized? false :jurisdiction "JPN" :status :intake}
    "student-4" {:id "student-4" :student-name "田中一郎"
                :practice-hours-completed 120 :practice-hours-required 100
                :child-performer-work-permit-unresolved? true
                :certification-finalized? false :jurisdiction "JPN" :status :intake}}})

;; ----------------------------- shared commit logic -----------------------------

(defn- finalize-certification!
  "Backend-agnostic `:student/mark-finalized` -- looks up the student
  via the protocol and drafts the certification-finalization record,
  and returns {:result .. :student-patch ..} for the caller to
  persist."
  [s student-id]
  (let [st (student s student-id)
        seq-n (next-sequence s (:jurisdiction st))
        result (registry/register-certification-finalization student-id (:jurisdiction st) seq-n)]
    {:result result
     :student-patch {:certification-finalized? true
                     :certification-number (get result "certification_number")}}))

;; ----------------------------- MemStore (default) -----------------------------

(defrecord MemStore [a]
  Store
  (student [_ id] (get-in @a [:students id]))
  (all-students [_] (sort-by :id (vals (:students @a))))
  (permit-screen-of [_ id] (get-in @a [:permit-screens id]))
  (curriculum-of [_ student-id] (get-in @a [:curricula student-id]))
  (ledger [_] (:ledger @a))
  (certification-history [_] (:certifications @a))
  (next-sequence [_ jurisdiction] (get-in @a [:sequences jurisdiction] 0))
  (student-already-finalized? [_ student-id] (boolean (get-in @a [:students student-id :certification-finalized?])))
  (commit-record! [s {:keys [effect path value payload]}]
    (case effect
      :student/upsert
      (swap! a update-in [:students (:id value)] merge value)

      :curriculum/set
      (swap! a assoc-in [:curricula (first path)] payload)

      :permit/set
      (swap! a assoc-in [:permit-screens (first path)] payload)

      :student/mark-finalized
      (let [student-id (first path)
            {:keys [result student-patch]} (finalize-certification! s student-id)
            jurisdiction (:jurisdiction (student s student-id))]
        (swap! a (fn [state]
                   (-> state
                       (update-in [:sequences jurisdiction] (fnil inc 0))
                       (update-in [:students student-id] merge student-patch)
                       (update :certifications registry/append result))))
        result)
      nil)
    s)
  (append-ledger! [_ fact] (swap! a update :ledger conj fact) fact)
  (with-students [s students] (when (seq students) (swap! a assoc :students students)) s))

(defn seed-db
  "A MemStore seeded with the demo student set. The deterministic
  default."
  []
  (->MemStore (atom (assoc (demo-data)
                           :curricula {} :permit-screens {} :ledger [] :sequences {}
                           :certifications []))))

;; ----------------------------- DatomicStore (langchain.db) -----------------------------

(def ^:private schema
  "DataScript/Datomic-style schema: only constraint attrs are declared.
  Compound values (curriculum/permit-screen payloads, ledger facts,
  certification records) are stored as EDN strings so `langchain.db`
  doesn't expand them into sub-entities -- the same convention every
  sibling actor's store uses."
  {:student/id                {:db/unique :db.unique/identity}
   :curriculum/student-id      {:db/unique :db.unique/identity}
   :permit/student-id            {:db/unique :db.unique/identity}
   :ledger/seq                     {:db/unique :db.unique/identity}
   :certification/seq                {:db/unique :db.unique/identity}
   :sequence/jurisdiction               {:db/unique :db.unique/identity}})

(defn- enc [v] (pr-str v))
(defn- dec* [s] (when s (edn/read-string s)))

(defn- student->tx [{:keys [id student-name practice-hours-completed practice-hours-required
                           child-performer-work-permit-unresolved? certification-finalized?
                           jurisdiction status certification-number]}]
  (cond-> {:student/id id}
    student-name                                       (assoc :student/student-name student-name)
    practice-hours-completed                             (assoc :student/practice-hours-completed practice-hours-completed)
    practice-hours-required                                (assoc :student/practice-hours-required practice-hours-required)
    (some? child-performer-work-permit-unresolved?)          (assoc :student/child-performer-work-permit-unresolved? child-performer-work-permit-unresolved?)
    (some? certification-finalized?)                           (assoc :student/certification-finalized? certification-finalized?)
    jurisdiction                                                 (assoc :student/jurisdiction jurisdiction)
    status                                                         (assoc :student/status status)
    certification-number                                            (assoc :student/certification-number certification-number)))

(def ^:private student-pull
  [:student/id :student/student-name :student/practice-hours-completed :student/practice-hours-required
   :student/child-performer-work-permit-unresolved? :student/certification-finalized?
   :student/jurisdiction :student/status :student/certification-number])

(defn- pull->student [m]
  (when (:student/id m)
    {:id (:student/id m) :student-name (:student/student-name m)
     :practice-hours-completed (:student/practice-hours-completed m)
     :practice-hours-required (:student/practice-hours-required m)
     :child-performer-work-permit-unresolved? (boolean (:student/child-performer-work-permit-unresolved? m))
     :certification-finalized? (boolean (:student/certification-finalized? m))
     :jurisdiction (:student/jurisdiction m) :status (:student/status m)
     :certification-number (:student/certification-number m)}))

(defrecord DatomicStore [conn]
  Store
  (student [_ id]
    (pull->student (d/pull (d/db conn) student-pull [:student/id id])))
  (all-students [_]
    (->> (d/q '[:find [?id ...] :where [?e :student/id ?id]] (d/db conn))
         (map #(pull->student (d/pull (d/db conn) student-pull [:student/id %])))
         (sort-by :id)))
  (permit-screen-of [_ id]
    (dec* (d/q '[:find ?p . :in $ ?sid
                :where [?k :permit/student-id ?sid] [?k :permit/payload ?p]]
              (d/db conn) id)))
  (curriculum-of [_ student-id]
    (dec* (d/q '[:find ?p . :in $ ?sid
                :where [?a :curriculum/student-id ?sid] [?a :curriculum/payload ?p]]
              (d/db conn) student-id)))
  (ledger [_]
    (->> (d/q '[:find ?s ?f :where [?e :ledger/seq ?s] [?e :ledger/fact ?f]] (d/db conn))
         (sort-by first)
         (mapv (comp dec* second))))
  (certification-history [_]
    (->> (d/q '[:find ?s ?r :where [?e :certification/seq ?s] [?e :certification/record ?r]] (d/db conn))
         (sort-by first)
         (mapv (comp dec* second))))
  (next-sequence [_ jurisdiction]
    (or (d/q '[:find ?n . :in $ ?j
              :where [?e :sequence/jurisdiction ?j] [?e :sequence/next ?n]]
            (d/db conn) jurisdiction)
        0))
  (student-already-finalized? [s student-id]
    (boolean (:certification-finalized? (student s student-id))))
  (commit-record! [s {:keys [effect path value payload]}]
    (case effect
      :student/upsert
      (d/transact! conn [(student->tx value)])

      :curriculum/set
      (d/transact! conn [{:curriculum/student-id (first path) :curriculum/payload (enc payload)}])

      :permit/set
      (d/transact! conn [{:permit/student-id (first path) :permit/payload (enc payload)}])

      :student/mark-finalized
      (let [student-id (first path)
            {:keys [result student-patch]} (finalize-certification! s student-id)
            jurisdiction (:jurisdiction (student s student-id))
            next-n (inc (next-sequence s jurisdiction))]
        (d/transact! conn
                     [(student->tx (assoc student-patch :id student-id))
                      {:sequence/jurisdiction jurisdiction :sequence/next next-n}
                      {:certification/seq (count (certification-history s)) :certification/record (enc (get result "record"))}])
        result)
      nil)
    s)
  (append-ledger! [s fact]
    (d/transact! conn [{:ledger/seq (count (ledger s)) :ledger/fact (enc fact)}])
    fact)
  (with-students [s students]
    (when (seq students) (d/transact! conn (mapv student->tx (vals students)))) s))

(defn datomic-store
  "A DatomicStore (langchain.db backend) seeded from `data`
  ({:students ..}); empty when omitted."
  ([] (datomic-store {}))
  ([{:keys [students]}]
   (let [s (->DatomicStore (d/create-conn schema))]
     (with-students s students))))

(defn datomic-seed-db
  "A DatomicStore seeded with the demo student set -- the Datomic-
  backed analog of `seed-db`, used to prove protocol parity."
  []
  (datomic-store (demo-data)))
