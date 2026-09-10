(ns cultural.facts
  "Per-jurisdiction cultural-education/child-performer regulatory
  catalog -- the G2-style spec-basis table the Instruction Integrity
  Governor checks every `:curriculum/verify` proposal against ('did
  the advisor cite an OFFICIAL public source for this jurisdiction's
  cultural-education/child-performer framework, or did it invent
  one?').

  Coverage is reported HONESTLY (see `coverage`), the same discipline
  every sibling actor's `facts` namespace uses: a jurisdiction not in
  this table has NO spec-basis, full stop -- the advisor must not
  fabricate one, and the governor holds if it tries.

  Seed values are drawn from each jurisdiction's official child-
  labor/child-performer authority (see `:provenance`); they are a
  STARTING catalog, not a from-scratch survey of all ~194
  jurisdictions. Extending coverage is additive: add one map to
  `catalog`, cite a real source, done -- never invent a jurisdiction's
  requirements to make coverage look bigger.")

(def catalog
  "iso3 -> requirement map. `:required-evidence` mirrors the student-
  enrollment-consent/curriculum/child-performer-permit-verification/
  certification-completion evidence set this blueprint's own Offer
  names; `:legal-basis` / `:owner-authority` / `:provenance` are the
  G2 citation the governor requires before any `:actuation/finalize-
  certification` proposal can commit."
  {"JPN" {:name "Japan"
          :owner-authority "労働基準監督署 (Labour Standards Inspection Office), 厚生労働省"
          :legal-basis "労働基準法 (Labor Standards Act) 第56条・第57条・第60条・第61条 -- 児童の演芸等の労働に関する許可・年少者証明書"
          :national-spec "満13歳未満の児童を演芸の事業に使用する場合の労働基準監督署長の許可義務"
          :provenance "https://elaws.e-gov.go.jp/document?lawid=322AC0000000049"
          :required-evidence ["受講同意記録 (student-enrollment-consent-record)"
                              "カリキュラム記録 (curriculum-record)"
                              "児童演芸許可確認記録 (child-performer-permit-verification-record)"
                              "修了認定記録 (certification-completion-record)"]}
   "USA" {:name "United States"
          :owner-authority "California Division of Labor Standards Enforcement (DLSE), Department of Industrial Relations"
          :legal-basis "California Labor Code §1308.5 et seq. -- Child Performer Services Permit / Entertainment Work Permit"
          :national-spec "Permit required before a minor may be employed or perform in entertainment/cultural-performance settings"
          :provenance "https://www.dir.ca.gov/dlse/childlaborlawpamphlet.pdf"
          :required-evidence ["Student enrollment consent record"
                              "Curriculum record"
                              "Child-performer-permit-verification record"
                              "Certification-completion record"]}
   "GBR" {:name "United Kingdom"
          :owner-authority "Department for Education (DfE) / local authority licensing"
          :legal-basis "Children (Performances and Activities) Regulations 2014"
          :national-spec "Local-authority performance licence required before a child performs in public/recorded cultural performances"
          :provenance "https://www.gov.uk/child-employment"
          :required-evidence ["Student enrollment consent record"
                              "Curriculum record"
                              "Child-performer-permit-verification record"
                              "Certification-completion record"]}
   "DEU" {:name "Germany"
          :owner-authority "Gewerbeaufsichtsamt / Bundesministerium für Arbeit und Soziales (BMAS)"
          :legal-basis "Jugendarbeitsschutzgesetz (JArbSchG) §§1, 6 -- Ausnahmebewilligung für Kinder bei künstlerischen Veranstaltungen"
          :national-spec "Behördliche Ausnahmegenehmigung vor öffentlichen Auftritten von Kindern im Rahmen kultureller Ausbildung"
          :provenance "https://www.gesetze-im-internet.de/jarbschg/__6.html"
          :required-evidence ["Einwilligungsprotokoll (student-enrollment-consent-record)"
                              "Lehrplanprotokoll (curriculum-record)"
                              "Auftrittsgenehmigungsprotokoll (child-performer-permit-verification-record)"
                              "Abschlusszertifizierungsprotokoll (certification-completion-record)"]}})

(defn spec-basis
  "The jurisdiction's requirement map, or nil -- nil means NO spec-basis,
  and the governor must hold any proposal that tries to finalize a
  certification on it."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report: how many of the requested jurisdictions actually
  have a spec-basis entry. Never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-isic-8542 R0: " (count catalog)
                 " jurisdictions seeded with an official spec-basis. "
                 "This is a starting catalog, not a survey of all ~194 "
                 "jurisdictions -- extend `cultural.facts/catalog`, "
                 "never fabricate a jurisdiction's requirements.")})))

(defn required-evidence-satisfied?
  "Does `submitted` (a set/coll of evidence keywords or strings) satisfy
  every evidence item listed for `iso3`? Missing spec-basis -> never
  satisfied."
  [iso3 submitted]
  (when-let [{:keys [required-evidence]} (spec-basis iso3)]
    (let [need (count required-evidence)
          have (count (filter (set submitted) required-evidence))]
      (= need have))))

(defn evidence-checklist [iso3]
  (:required-evidence (spec-basis iso3) []))
