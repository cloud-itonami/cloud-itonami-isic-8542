(ns cultural.registry-test
  (:require [clojure.test :refer [deftest is]]
            [cultural.registry :as r]))

;; ----------------------------- practice-hours-insufficient? -----------------------------

(deftest not-insufficient-when-hours-met
  (is (not (r/practice-hours-insufficient?
            {:practice-hours-completed 100 :practice-hours-required 100})))
  (is (not (r/practice-hours-insufficient?
            {:practice-hours-completed 120 :practice-hours-required 100}))))

(deftest insufficient-when-hours-short
  (is (r/practice-hours-insufficient?
       {:practice-hours-completed 50 :practice-hours-required 100})))

(deftest missing-fields-are-not-treated-as-insufficient
  (is (not (r/practice-hours-insufficient? {})))
  (is (not (r/practice-hours-insufficient? {:practice-hours-completed 50}))))

;; ----------------------------- register-certification-finalization -----------------------------

(deftest finalization-is-a-draft-not-a-real-finalization
  (let [result (r/register-certification-finalization "student-1" "JPN" 0)]
    (is (nil? (get-in result ["certificate" "proof"])))
    (is (= (get-in result ["certificate" "issued_by_registry"]) false))
    (is (= (get-in result ["certificate" "status"]) "draft-unsigned"))))

(deftest finalization-assigns-certification-number
  (let [result (r/register-certification-finalization "student-1" "JPN" 7)]
    (is (= (get result "certification_number") "JPN-CERT-000007"))
    (is (= (get-in result ["record" "student_id"]) "student-1"))
    (is (= (get-in result ["record" "kind"]) "certification-finalization-draft"))
    (is (= (get-in result ["record" "immutable"]) true))))

(deftest finalization-validation-rules
  (is (thrown? Exception (r/register-certification-finalization "" "JPN" 0)))
  (is (thrown? Exception (r/register-certification-finalization "student-1" "" 0)))
  (is (thrown? Exception (r/register-certification-finalization "student-1" "JPN" -1))))

(deftest history-is-append-only
  (let [c1 (r/register-certification-finalization "student-1" "JPN" 0)
        hist (r/append [] c1)
        c2 (r/register-certification-finalization "student-2" "JPN" 1)
        hist2 (r/append hist c2)]
    (is (= 2 (count hist2)))
    (is (= "JPN-CERT-000000" (get-in hist2 [0 "record_id"])))
    (is (= "JPN-CERT-000001" (get-in hist2 [1 "record_id"])))))
