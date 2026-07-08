# ADR-0001: StudioEdOps-LLM ⊣ Instruction Integrity Governor architecture

## Status

Accepted. `cloud-itonami-isic-8542` promoted from `:blueprint` to
`:implemented` in the `kotoba-lang/industry` registry.

## Context

`cloud-itonami-isic-8542` publishes an OSS business blueprint for
cultural education: instruction in music, arts, dance, language and
other cultural skills. Like every prior actor in this fleet, the
blueprint alone is not an implementation: this ADR records the
governed-actor architecture that promotes it to real, tested code,
following the same langgraph StateGraph + independent Governor +
Phase 0→3 rollout pattern established by `cloud-itonami-isic-6511`
(life insurance) and applied across sixty-seven prior siblings, most
recently `cloud-itonami-isic-8790` (other residential care
activities).

## Decision

### Decision 1: single-actuation shape

This blueprint's own README/business-model.md/operator-guide.md
consistently name only ONE real-world act: "finalizing a
certification or progress record." Following `sports`/8541's
precedent for this same either/or-naming ambiguity, this build treats
"certification or progress record" as ONE conceptual act. Matching
`leasing`/`underwriting`/`testlab`/`clinic`/`veterinary`/`funeral`/
`parksafety`/`salon`/`entertainment`/`facility`/`consulting`/
`advertising`/`polling`/`research`/`design`/`sports`/`alliedhealth`/
`photo`/`personalservice`/`edsupport`'s single-actuation shape,
`high-stakes` here is a one-member set, `#{:actuation/finalize-
certification}`.

### Decision 2: entity and op shape

The primary entity is a `student`, matching the business-model.md's
own Offer language ("student enrollment intake", "curriculum/program
proposal", "certification/progress proposal"). Four ops: `:student/
intake` (directory upsert, no capital risk), `:curriculum/verify`
(per-jurisdiction cultural-education/child-performer evidence
checklist, never auto), `:permit/screen` (child-performer-work-permit
screening, unconditional-evaluation discipline, never auto), and
`:actuation/finalize-certification` (POSITIVE, high-stakes --
finalizing a real certification or progress record).

### Decision 3: `child-performer-work-permit-unresolved-violations` -- the 53rd unconditional-evaluation screening grounding, a genuinely new concept

Before writing this check, every prior sibling's governor/registry
namespaces were grepped for "child-performer", "performer-license"
and "entertainment-work-permit" -- zero hits, confirming this is a
genuinely new unconditional-evaluation concept, avoiding the false-
precedent-claim risk `leasing`'s ADR-0001 documents.
`child-performer-work-permit-unresolved-violations` reuses the
unconditional-evaluation DISCIPLINE (`casualty.governor/sanctions-
violations`'s original fix) for the 53rd distinct application
overall, continuing the count established across this fleet's builds
(most recently `residential.governor/background-check-not-cleared-
violations` at 52nd). Grounded in real child-performer labor law:
California Labor Code §1308.5 (Child Performer Services Permit), UK's
Children (Performances and Activities) Regulations 2014, Japan's
Labor Standards Act Article 56/57, and Germany's JArbSchG §6. Gates
`:permit/screen` and the actuation.

### Decision 4: `practice-hours-insufficient?` -- an honest ninth MINIMUM-threshold instance, not claimed as new

`veterinary`/`funeral`/`hospital` established the first three
(temporal) instances of this fleet's MINIMUM-threshold sufficiency
check family; `association`/`secondary`/`polling`/`research`
generalized it to non-temporal ground truths as the fourth through
seventh; `personalservice`'s cooling-off-period-not-elapsed? returned
to a temporal ground truth as the eighth. `cultural.registry/
practice-hours-insufficient?` is the NINTH instance, directly
analogous to `secondary.registry/attendance-hours-insufficient?`'s
own hours-completed/hours-required shape -- not claimed as new. Gates
only the actuation (a pure ground-truth recompute, no dedicated
screening op needed, matching `secondary`/8521's own scoping).

### Decision 5: dedicated double-actuation-guard boolean

`:certification-finalized?` is a dedicated boolean on the `student`
record, never a single `:status` value -- the same discipline every
prior sibling governor's guards establish, informed by `cloud-
itonami-isic-6492`'s status-lifecycle bug (ADR-2607071320).

### Decision 6: Store protocol, MemStore + DatomicStore parity

`cultural.store/Store` is implemented by both `MemStore` (atom-
backed, default for dev/tests/demo) and `DatomicStore` (`langchain.
db`-backed), proven to satisfy the same contract in `test/cultural/
store_contract_test.clj` -- the same seam every sibling actor uses so
swapping the SSoT backend is a configuration change, not a rewrite.
The protocol's per-entity accessor is named `student` directly -- not
a Clojure special form, so no `-of` suffix workaround was needed.

### Decision 7: Phase 0→3 rollout

Phase 3's `:auto` set has exactly one member, `:student/intake` (no
capital risk). `:curriculum/verify` and `:permit/screen` are never
auto-eligible at any phase (matching every sibling's screening-op
posture), and `:actuation/finalize-certification` is permanently
excluded from every phase's `:auto` set -- a structural fact, not a
rollout milestone, enforced by BOTH `cultural.phase` and `cultural.
governor`'s `high-stakes` set independently.

### Decision 8: no bespoke domain capability lib

This blueprint's own `:itonami.blueprint/required-technologies` names
no domain-specific capability beyond the generic robotics/identity/
forms/dmn/bpmn/audit-ledger stack -- there was no capability-lib
decision to make at all.

### Decision 9: mock + LLM advisor pair

`cultural.culturaladvisor` provides `mock-advisor` (deterministic,
default everywhere -- the actor graph and governor contract run
offline) and `llm-advisor` (backed by `langchain.model/ChatModel`,
with a defensive EDN-proposal parser so a malformed LLM response
degrades to a safe low-confidence noop rather than ever auto-
finalizing a certification).

### Decision 10: no `blueprint.edn` field-sync fixes needed

Matching `photo`/7420's, `personalservice`/9609's, `edsupport`/8550's,
`headoffice`/7010's and `residential`/8790's own experience, this
repo's `blueprint.edn` already had the correct `isic-` prefixed `:id`
and correctly populated `:required-technologies`/`:optional-
technologies` matching the `kotoba-lang/industry` registry's own
entry for `"8542"` exactly -- only the `:maturity` field itself
needed adding.

## Alternatives considered

- **A dual-actuation shape** (splitting "certification" and
  "progress record" into two acts). Rejected: the blueprint's own
  text consistently names only ONE real-world act; following
  `sports`/8541's precedent, this either/or naming is treated as one
  conceptual act, not grounds for inventing a second.
- **Framing `child-performer-work-permit-unresolved?` as a reuse of
  `photo`/7420's `minor-subject-guardian-consent-unresolved?`
  concept.** Rejected: guardian consent (does a parent consent to a
  minor's image being used) and a child-performer work permit (does a
  labor authority authorize a minor to perform/work at all) are
  distinct real-world regulatory regimes -- consent law vs. child-
  labor law -- confirmed via grep to have zero prior instances under
  either name for this specific concept.
- **Reusing `secondary`/8521's `attendance-hours-insufficient?`
  literally without renaming.** Rejected in favor of a domain-
  appropriate rename (`practice-hours-insufficient?`) while HONESTLY
  documenting the reuse, matching `holdco`/6420's precedent for
  "reused, renamed for domain fit."

## Consequences

- Sixty-eighth actor in this fleet (67 implemented before this
  build).
- Establishes a genuinely NEW unconditional-evaluation-screening
  concept (child-performer-work-permit-unresolved), grep-verified
  absent from every prior sibling before the claim was finalized.
- Documents an honest NINTH instance of the MINIMUM-threshold
  sufficiency check family, not claimed as new.
- `MemStore` ‖ `DatomicStore` parity is proven by `test/cultural/
  store_contract_test.clj`, the same `:db-api`-driven swap pattern
  every sibling actor uses.
- `blueprint.edn` required no field-sync fixes this time (already
  correct) -- only the `:maturity` flip itself.

## References

- `orgs/cloud-itonami/cloud-itonami-isic-8542/README.md`
- `orgs/cloud-itonami/cloud-itonami-isic-8542/docs/business-model.md`
- `orgs/kotoba-lang/industry/resources/kotoba/industry/registry.edn` (entry `"8542"`)
