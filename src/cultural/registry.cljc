(ns cultural.registry
  "Pure-function certification/progress-record finalization
  construction -- an append-only cultural-education book-of-record
  draft.

  Like every sibling actor's registry, there is no single
  international check-digit standard for a certification reference
  number -- every school/jurisdiction assigns its own reference
  format. This namespace does NOT invent one; it builds a
  jurisdiction-scoped sequence number and validates the record's
  required fields, the same honest, non-fabricating discipline
  `cultural.facts` uses.

  `practice-hours-insufficient?` is an HONEST reuse of this fleet's
  MINIMUM-threshold sufficiency check family (`veterinary.registry/
  withdrawal-period-insufficient?`/`funeral.registry/waiting-period-
  elapsed?`/`hospital.registry/observation-period-elapsed?`
  established the first three, temporal; `association.registry/
  continuing-education-hours-insufficient?`/`secondary.registry/
  attendance-hours-insufficient?`/`polling.registry/sample-size-
  insufficient?`/`research.registry/replication-count-insufficient?`
  generalized it to non-temporal ground truths as the fourth through
  seventh; `personalservice.registry/cooling-off-period-not-elapsed?`
  returned to a temporal ground truth as the eighth) -- the NINTH
  instance overall, applying the SAME lo-bound comparison to a
  student's own recorded completed practice hours against their own
  recorded required practice hours, directly analogous to
  `secondary.registry/attendance-hours-insufficient?`'s own hours-
  completed/hours-required shape (both compare a student's own
  progress against a jurisdiction-set minimum), not claimed as new.

  The `child-performer-work-permit-unresolved?` concept (a GENUINELY
  NEW unconditional-evaluation check, grep-verified absent -- no
  'child-performer'/'performer-license'/'entertainment-work-permit'
  concept exists anywhere else in this fleet) is a BOOLEAN flag read
  directly off the student's own record by `cultural.governor` -- the
  same shape `photo.governor`'s `minor-subject-guardian-consent-
  unresolved-violations` and `residential.governor`'s `mandatory-
  reporting-obligation-unresolved-violations` use, neither of which
  needed a dedicated registry-level predicate either -- so it is NOT
  defined here.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real school-management system. It builds the RECORD a
  cultural-education operator would keep, not the act of finalizing
  the certification itself (that is `cultural.operation`'s
  `:actuation/finalize-certification`, always human-gated -- see
  README `Actuation`)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is the
  cultural-education operator's own act, not this actor's. See README
  `Actuation`."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn practice-hours-insufficient?
  "Does `student`'s own `:practice-hours-completed` fall short of the
  jurisdiction's own recorded `:practice-hours-required` minimum? A
  pure ground-truth check against the student's own permanent fields
  -- no upstream comparison needed. The NINTH instance of this
  fleet's MINIMUM-threshold sufficiency family (see ns docstring),
  not claimed as new."
  [{:keys [practice-hours-completed practice-hours-required]}]
  (and (number? practice-hours-completed) (number? practice-hours-required)
       (< practice-hours-completed practice-hours-required)))

(defn register-certification-finalization
  "Validate + construct the CERTIFICATION-FINALIZATION registration
  DRAFT -- the cultural-education operator's own act of finalizing a
  real certification or progress record. Pure function -- does not
  touch any real school-management system; it builds the RECORD an
  operator would keep. `cultural.governor` independently re-verifies
  the student's own practice-hours ground truth and child-performer-
  permit status, and blocks a double-finalization for the same
  student, before this is ever allowed to commit."
  [student-id jurisdiction sequence]
  (when-not (and student-id (not= student-id ""))
    (throw (ex-info "certification-finalization: student_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "certification-finalization: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "certification-finalization: sequence must be >= 0" {})))
  (let [certification-number (str (str/upper-case jurisdiction) "-CERT-" (zero-pad sequence 6))
        record {"record_id" certification-number
                "kind" "certification-finalization-draft"
                "student_id" student-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "certification_number" certification-number
     "certificate" (unsigned-certificate "CertificationFinalization" certification-number certification-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
