(ns cultural.store-contract-test
  "The Store contract, run against BOTH backends. Proving MemStore and
  the Datomic-backed (langchain.db) store satisfy the same contract is
  what makes 'swap the SSoT for Datomic / kotoba-server' a
  configuration change, not a rewrite -- see `cloud-itonami-isic-6511`'s
  `underwriting.store-contract-test` for the same pattern on the
  sibling actor."
  (:require [clojure.test :refer [deftest is testing]]
            [cultural.store :as store]))

(defn- backends []
  [["MemStore" (store/seed-db)] ["DatomicStore" (store/datomic-seed-db)]])

(deftest read-parity
  (doseq [[label s] (backends)]
    (testing label
      (is (= "Sato Kenji" (:student-name (store/student s "student-1"))))
      (is (= "JPN" (:jurisdiction (store/student s "student-1"))))
      (is (= 120 (:practice-hours-completed (store/student s "student-1"))))
      (is (false? (:child-performer-work-permit-unresolved? (store/student s "student-1"))))
      (is (= 50 (:practice-hours-completed (store/student s "student-3"))))
      (is (true? (:child-performer-work-permit-unresolved? (store/student s "student-4"))))
      (is (false? (:certification-finalized? (store/student s "student-1"))))
      (is (= ["student-1" "student-2" "student-3" "student-4"]
             (mapv :id (store/all-students s))))
      (is (nil? (store/permit-screen-of s "student-1")))
      (is (nil? (store/curriculum-of s "student-1")))
      (is (= [] (store/ledger s)))
      (is (= [] (store/certification-history s)))
      (is (zero? (store/next-sequence s "JPN")))
      (is (false? (store/student-already-finalized? s "student-1"))))))

(deftest write-and-ledger-parity
  (doseq [[label s] (backends)]
    (testing label
      (testing "partial upsert merges, preserving untouched fields"
        (store/commit-record! s {:effect :student/upsert
                                 :value {:id "student-1" :student-name "Sato Kenji"}})
        (is (= "Sato Kenji" (:student-name (store/student s "student-1"))))
        (is (= 120 (:practice-hours-completed (store/student s "student-1"))) "unrelated field preserved"))
      (testing "curriculum / permit-screen payloads commit and read back"
        (store/commit-record! s {:effect :curriculum/set :path ["student-1"]
                                 :payload {:jurisdiction "JPN" :checklist ["a" "b"]}})
        (is (= {:jurisdiction "JPN" :checklist ["a" "b"]} (store/curriculum-of s "student-1")))
        (store/commit-record! s {:effect :permit/set :path ["student-1"]
                                 :payload {:student-id "student-1" :child-performer-work-permit-unresolved? false}})
        (is (= {:student-id "student-1" :child-performer-work-permit-unresolved? false} (store/permit-screen-of s "student-1"))))
      (testing "certification finalization drafts a record and advances the sequence"
        (store/commit-record! s {:effect :student/mark-finalized :path ["student-1"]})
        (is (= "JPN-CERT-000000" (get (first (store/certification-history s)) "record_id")))
        (is (= "certification-finalization-draft" (get (first (store/certification-history s)) "kind")))
        (is (true? (:certification-finalized? (store/student s "student-1"))))
        (is (= 1 (count (store/certification-history s))))
        (is (= 1 (store/next-sequence s "JPN")))
        (is (true? (store/student-already-finalized? s "student-1")))
        (is (false? (store/student-already-finalized? s "student-2"))))
      (testing "ledger is append-only and order-preserving"
        (store/append-ledger! s {:op :a :disposition :commit})
        (store/append-ledger! s {:op :b :disposition :hold})
        (is (= [:commit :hold] (mapv :disposition (store/ledger s))))))))

(deftest datomic-empty-store-is-usable
  (let [s (store/datomic-store)]
    (is (nil? (store/student s "nope")))
    (is (= [] (store/all-students s)))
    (is (= [] (store/ledger s)))
    (is (= [] (store/certification-history s)))
    (is (zero? (store/next-sequence s "JPN")))
    (store/with-students s {"x" {:id "x" :student-name "n"
                                 :practice-hours-completed 100 :practice-hours-required 100
                                 :child-performer-work-permit-unresolved? false
                                 :certification-finalized? false :jurisdiction "JPN" :status :intake}})
    (is (= "n" (:student-name (store/student s "x"))))))
