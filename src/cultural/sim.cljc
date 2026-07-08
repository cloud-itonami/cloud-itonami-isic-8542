(ns cultural.sim
  "Demo driver -- `clojure -M:dev:run`. Walks a clean student through
  intake -> curriculum verification -> child-performer-permit
  screening -> certification-finalization proposal (always escalates)
  -> human approval -> commit, then shows four HARD holds (a
  jurisdiction with no spec-basis, a student whose own recorded
  practice hours fall short of their own recorded requirement, a
  student whose own recorded child-performer work permit has NOT been
  resolved [screened directly via `:permit/screen` -- never via an
  actuation op against an unscreened student -- see this actor's own
  governor ns docstring / the lesson `parksafety`'s ADR-2607071922
  Decision 5, `eldercare`'s, `museum`'s, `conservation`'s, `salon`'s,
  `entertainment`'s, `casework`'s, `hospital`'s, `facility`'s,
  `school`'s, `association`'s, `leasing`'s, `behavioral`'s,
  `secondary`'s, `card`'s, `water`'s, `telecom`'s, `aerospace`'s,
  `recovery`'s, `consulting`'s, `union`'s, `congregation`'s, `fab`'s,
  `energy`'s, `care`'s, `navigator`'s, `learning`'s, `banking`'s,
  `advertising`'s, `polling`'s, `research`'s, `design`'s, `nursing`'s,
  `sports`'s, `alliedhealth`'s, `laundry`'s, `holdco`'s, `photo`'s,
  `personalservice`'s, `edsupport`'s, `headoffice`'s and
  `residential`'s ADR-0001s already recorded], and a double
  finalization of an already-processed student) that never reach a
  human at all, and prints the audit ledger + the draft certification-
  finalization records."
  (:require [langgraph.graph :as g]
            [cultural.store :as store]
            [cultural.operation :as op]))

(def operator {:actor-id "op-1" :actor-role :licensed-educator :phase 3})

(defn- exec! [actor tid request context]
  (g/run* actor {:request request :context context} {:thread-id tid}))

(defn- approve! [actor tid]
  (g/run* actor {:approval {:status :approved :by "op-1"}} {:thread-id tid :resume? true}))

(defn -main [& _]
  (let [db (store/seed-db)
        actor (op/build db)]
    (println "== student/intake student-1 (JPN, clean; practice hours sufficient, permit resolved) ==")
    (println (exec! actor "t1" {:op :student/intake :subject "student-1"
                                :patch {:id "student-1" :student-name "Sato Kenji"}} operator))

    (println "== curriculum/verify student-1 (escalates -- human approves) ==")
    (println (exec! actor "t2" {:op :curriculum/verify :subject "student-1"} operator))
    (println (approve! actor "t2"))

    (println "== permit/screen student-1 (clean; escalates -- human approves) ==")
    (println (exec! actor "t3" {:op :permit/screen :subject "student-1"} operator))
    (println (approve! actor "t3"))

    (println "== actuation/finalize-certification student-1 (always escalates -- actuation/finalize-certification) ==")
    (let [r (exec! actor "t4" {:op :actuation/finalize-certification :subject "student-1"} operator)]
      (println r)
      (println "-- human licensed educator approves --")
      (println (approve! actor "t4")))

    (println "== curriculum/verify student-2 (no spec-basis -> HARD hold) ==")
    (println (exec! actor "t5" {:op :curriculum/verify :subject "student-2" :no-spec? true} operator))

    (println "== curriculum/verify student-3 (escalates -- human approves; sets up the practice-hours test) ==")
    (println (exec! actor "t6" {:op :curriculum/verify :subject "student-3"} operator))
    (println (approve! actor "t6"))

    (println "== actuation/finalize-certification student-3 (practice hours 50 < required 100 -> HARD hold) ==")
    (println (exec! actor "t7" {:op :actuation/finalize-certification :subject "student-3"} operator))

    (println "== permit/screen student-4 (unresolved -> HARD hold, never reaches a human) ==")
    (println (exec! actor "t8" {:op :permit/screen :subject "student-4"} operator))

    (println "== actuation/finalize-certification student-1 AGAIN (double-finalization -> HARD hold) ==")
    (println (exec! actor "t9" {:op :actuation/finalize-certification :subject "student-1"} operator))

    (println "== audit ledger ==")
    (doseq [f (store/ledger db)] (println f))

    (println "== draft certification-finalization records ==")
    (doseq [r (store/certification-history db)] (println r))))
