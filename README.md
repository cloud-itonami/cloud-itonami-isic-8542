# cloud-itonami-isic-8542

Open Business Blueprint for **ISIC Rev.5 8542**: Cultural education.

This repository publishes a cultural-education actor -- student
intake, per-jurisdiction cultural-education/child-performer
regulatory assessment, child-performer-permit screening and
certification/progress-record finalization -- as an OSS business that
any qualified, licensed operator can fork, deploy, run, improve and
sell, so a community or independent educator never surrenders student
data and ledgers to a closed SaaS.

Built on this workspace's
[`langgraph`](https://github.com/kotoba-lang/langgraph)
StateGraph runtime (portable `.cljc`, supervised superstep loop,
interrupts, Datomic/in-mem checkpoints) -- the same actor pattern as
every prior actor in this fleet
([`cloud-itonami-isic-6511`](https://github.com/cloud-itonami/cloud-itonami-isic-6511),
[`6512`](https://github.com/cloud-itonami/cloud-itonami-isic-6512),
[`6621`](https://github.com/cloud-itonami/cloud-itonami-isic-6621),
[`6622`](https://github.com/cloud-itonami/cloud-itonami-isic-6622),
[`6629`](https://github.com/cloud-itonami/cloud-itonami-isic-6629),
[`6520`](https://github.com/cloud-itonami/cloud-itonami-isic-6520),
[`6530`](https://github.com/cloud-itonami/cloud-itonami-isic-6530),
[`6820`](https://github.com/cloud-itonami/cloud-itonami-isic-6820),
[`6612`](https://github.com/cloud-itonami/cloud-itonami-isic-6612),
[`6492`](https://github.com/cloud-itonami/cloud-itonami-isic-6492),
[`6920`](https://github.com/cloud-itonami/cloud-itonami-isic-6920),
[`6611`](https://github.com/cloud-itonami/cloud-itonami-isic-6611),
[`7120`](https://github.com/cloud-itonami/cloud-itonami-isic-7120),
[`8620`](https://github.com/cloud-itonami/cloud-itonami-isic-8620),
[`8530`](https://github.com/cloud-itonami/cloud-itonami-isic-8530),
[`9200`](https://github.com/cloud-itonami/cloud-itonami-isic-9200),
[`7500`](https://github.com/cloud-itonami/cloud-itonami-isic-7500),
[`9603`](https://github.com/cloud-itonami/cloud-itonami-isic-9603),
[`9521`](https://github.com/cloud-itonami/cloud-itonami-isic-9521),
[`9321`](https://github.com/cloud-itonami/cloud-itonami-isic-9321),
[`8730`](https://github.com/cloud-itonami/cloud-itonami-isic-8730),
[`9102`](https://github.com/cloud-itonami/cloud-itonami-isic-9102),
[`9103`](https://github.com/cloud-itonami/cloud-itonami-isic-9103),
[`9602`](https://github.com/cloud-itonami/cloud-itonami-isic-9602),
[`9000`](https://github.com/cloud-itonami/cloud-itonami-isic-9000),
[`8890`](https://github.com/cloud-itonami/cloud-itonami-isic-8890),
[`8610`](https://github.com/cloud-itonami/cloud-itonami-isic-8610),
[`9311`](https://github.com/cloud-itonami/cloud-itonami-isic-9311),
[`8510`](https://github.com/cloud-itonami/cloud-itonami-isic-8510),
[`9412`](https://github.com/cloud-itonami/cloud-itonami-isic-9412),
[`6491`](https://github.com/cloud-itonami/cloud-itonami-isic-6491),
[`8720`](https://github.com/cloud-itonami/cloud-itonami-isic-8720),
[`8521`](https://github.com/cloud-itonami/cloud-itonami-isic-8521),
[`6619`](https://github.com/cloud-itonami/cloud-itonami-isic-6619),
[`3600`](https://github.com/cloud-itonami/cloud-itonami-isic-3600),
[`6190`](https://github.com/cloud-itonami/cloud-itonami-isic-6190),
[`3030`](https://github.com/cloud-itonami/cloud-itonami-isic-3030),
[`3830`](https://github.com/cloud-itonami/cloud-itonami-isic-3830),
[`7020`](https://github.com/cloud-itonami/cloud-itonami-isic-7020),
[`9420`](https://github.com/cloud-itonami/cloud-itonami-isic-9420),
[`9491`](https://github.com/cloud-itonami/cloud-itonami-isic-9491),
[`2610`](https://github.com/cloud-itonami/cloud-itonami-isic-2610),
[`3512`](https://github.com/cloud-itonami/cloud-itonami-isic-3512),
[`8810`](https://github.com/cloud-itonami/cloud-itonami-isic-8810),
[`8691`](https://github.com/cloud-itonami/cloud-itonami-isic-8691),
[`8569`](https://github.com/cloud-itonami/cloud-itonami-isic-8569),
[`6419`](https://github.com/cloud-itonami/cloud-itonami-isic-6419),
[`7310`](https://github.com/cloud-itonami/cloud-itonami-isic-7310),
[`7320`](https://github.com/cloud-itonami/cloud-itonami-isic-7320),
[`7210`](https://github.com/cloud-itonami/cloud-itonami-isic-7210),
[`7410`](https://github.com/cloud-itonami/cloud-itonami-isic-7410),
[`8710`](https://github.com/cloud-itonami/cloud-itonami-isic-8710),
[`8541`](https://github.com/cloud-itonami/cloud-itonami-isic-8541),
[`8690`](https://github.com/cloud-itonami/cloud-itonami-isic-8690),
[`9601`](https://github.com/cloud-itonami/cloud-itonami-isic-9601),
[`6420`](https://github.com/cloud-itonami/cloud-itonami-isic-6420),
[`7420`](https://github.com/cloud-itonami/cloud-itonami-isic-7420),
[`9609`](https://github.com/cloud-itonami/cloud-itonami-isic-9609),
[`8550`](https://github.com/cloud-itonami/cloud-itonami-isic-8550),
[`7010`](https://github.com/cloud-itonami/cloud-itonami-isic-7010),
[`8790`](https://github.com/cloud-itonami/cloud-itonami-isic-8790)) --
here it is **StudioEdOps-LLM ⊣ Instruction Integrity Governor**.

> **Why an actor layer at all?** An LLM is great at drafting a
> student-intake summary, normalizing records, and checking whether a
> student's own recorded practice hours actually satisfy their own
> recorded requirement -- but it has **no notion of which
> jurisdiction's cultural-education/child-performer law is official,
> no license to finalize a real certification or progress record, and
> no way to know on its own whether a required child-performer work
> permit has actually stayed resolved**. Letting it finalize a
> certification directly invites fabricated regulatory citations, a
> certification being finalized on top of insufficient practice
> hours, and an unresolved child-performer permit being quietly
> overlooked -- and liability, and child-labor-law risk, for whoever
> runs it. This project seals the StudioEdOps-LLM into a single node
> and wraps it with an independent **Instruction Integrity
> Governor**, a human **approval workflow**, and an immutable
> **audit ledger**.

## Scope: what this actor does and does not do

This actor covers student intake through cultural-education/child-
performer regulatory assessment, child-performer-permit screening and
certification/progress-record finalization. It does **not**, by
itself, hold any license required to operate as a cultural-education
provider in a given jurisdiction, and it does not claim to. It also
does not perform the actual music/arts/dance/language instruction
itself, or judge its artistic quality -- `cultural.registry/practice-
hours-insufficient?` is a pure ground-truth recompute against the
student's own recorded fields, not an artistic assessment. Whoever
deploys and operates a live instance (a licensed cultural-education
provider) supplies any jurisdiction-specific license, the real
instructional delivery and the real school-management-system
integrations, and bears that jurisdiction's liability -- the software
supplies the governed, spec-cited, audited execution scaffold so that
provider does not have to build the compliance layer from scratch.

### Actuation

**Finalizing a real certification or progress record is never
autonomous, at any phase, by construction.** Two independent layers
enforce this (`cultural.governor`'s `:actuation/finalize-
certification` high-stakes gate and `cultural.phase`'s phase table,
which never puts `:actuation/finalize-certification` in any phase's
`:auto` set) -- see `cultural.phase`'s docstring and `test/cultural/
phase_test.clj`'s `finalize-certification-never-auto-at-any-phase`.
The actor may draft, check and recommend; a human licensed educator
is always the one who actually finalizes a certification. Matching
`leasing`'s/`underwriting`'s/`testlab`'s/`clinic`'s/`veterinary`'s/
`funeral`'s/`parksafety`'s/`salon`'s/`entertainment`'s/`facility`'s/
`consulting`'s/`advertising`'s/`polling`'s/`research`'s/`design`'s/
`sports`'s/`alliedhealth`'s/`photo`'s/`personalservice`'s/
`edsupport`'s single-actuation shape, grounded directly in this
blueprint's own README text ("No automated proposal, by itself, can
complete the following without governor approval and audit evidence:
finalizing a certification or progress record") -- following
`sports`/8541's either/or-naming precedent, this build treats
"certification or progress record" as ONE conceptual act. A POSITIVE
actuation (finalizing a real record), matching this fleet's majority
actuation shape (`3600`/`6190` are the fleet's two NEGATIVE-actuation
exceptions).

## The core contract

```
student intake + jurisdiction facts (cultural.facts, spec-cited)
        |
        v
   ┌───────────────────────┐   proposal      ┌───────────────────────┐
   │ StudioEdOps-LLM       │ ─────────────▶ │ Instruction Integrity          │  (independent system)
   │ (sealed)              │  + citations    │ Governor:                    │
   └───────────────────────┘                 │ spec-basis · evidence-       │
          │                 commit ◀┼ incomplete · child-performer-     │
          │                         │ work-permit-unresolved              │
    record + ledger        escalate ┼ (unconditional, NEW) · practice-      │
          │              (ALWAYS for│ hours-insufficient (MINIMUM-           │
          │               :actuation│ threshold, honest reuse) ·               │
          │               /finalize-│ already-finalized                          │
          ▼               certif'n) └───────────────────────┘
      human approval
```

**The StudioEdOps-LLM never finalizes a certification the
Instruction Integrity Governor would reject, and never does so
without a human sign-off.** Hard violations (fabricated regulatory
requirements; unsupported evidence; an unresolved child-performer
work permit; insufficient practice hours; a double finalization)
force **hold** and *cannot* be approved past; a clean finalization
proposal still always routes to a human.

## Run

```bash
clojure -M:dev:run     # walk one clean single-actuation lifecycle + four HARD-hold cases through the actor
clojure -M:dev:test    # governor contract · phase invariants · store parity · registry conformance · facts coverage
clojure -M:lint        # clj-kondo (errors fail; CI mirrors this)
```

## Robotics premise

All cloud-itonami verticals are designed on the premise that a **robot
performs the physical domain work**. Here a studio-safety monitoring
robot supports physical supervision during activities, under the
actor, gated by the independent **Instruction Integrity Governor**.
The governor never dispatches hardware itself; `:high`/`:safety-
critical` actions require human sign-off.

## Open business

This repository is not only source code. It is a public, forkable
business model:

| Layer | What is open |
|---|---|
| OSS core | Actor runtime, Instruction Integrity Governor, certification-finalization draft records, audit ledger |
| Business blueprint | Customer, offer, pricing, unit economics, sales motion |
| Operator playbook | How to fork, license, deploy and support the service in a jurisdiction |
| Trust controls | Governance, security reporting, actuation invariant, audit requirements |

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md) to start this as an
open business on itonami.cloud, and
[`docs/adr/0001-architecture.md`](docs/adr/0001-architecture.md) for the
full architecture and decision record.

## Capability layer

This blueprint resolves its technology stack via
[`kotoba-lang/industry`](https://github.com/kotoba-lang/industry) (ISIC
`8542`). This vertical's student records are practice-specific rather
than a shared cross-operator data contract, so `cultural.*` runs on
the generic robotics/identity/forms/dmn/bpmn/audit-ledger stack only
-- no bespoke domain capability lib to reference at all.

## Layout

| File | Role |
|---|---|
| `src/cultural/store.cljc` | **Store** protocol -- `MemStore` ‖ `DatomicStore` (`langchain.db`) + append-only audit ledger + certification-finalization history. No dynamically-filed sub-record -- the actuation op acts directly on a pre-seeded student, and the double-actuation guard checks a dedicated `:certification-finalized?` boolean rather than a `:status` value |
| `src/cultural/registry.cljc` | Certification-finalization draft records, plus `practice-hours-insufficient?` -- an HONEST reuse of this fleet's MINIMUM-threshold sufficiency check family (the NINTH instance), directly analogous to `secondary.registry/attendance-hours-insufficient?`'s own shape, not claimed as new |
| `src/cultural/facts.cljc` | Per-jurisdiction cultural-education/child-performer catalog with an official spec-basis citation per entry, honest coverage reporting |
| `src/cultural/culturaladvisor.cljc` | **StudioEdOps-LLM** -- `mock-advisor` ‖ `llm-advisor`; intake/curriculum-verification/child-performer-permit-screening/certification-finalization proposals |
| `src/cultural/governor.cljc` | **Instruction Integrity Governor** -- 5 HARD checks (spec-basis · evidence-incomplete · child-performer-work-permit-unresolved, unconditional evaluation, GENUINELY NEW, the 53rd grounding of this discipline · practice-hours-insufficient, MINIMUM-threshold reuse, the 9th instance, not claimed as new · already-finalized guard) + 1 soft (confidence/actuation gate) |
| `src/cultural/phase.cljc` | **Phase 0→3** -- read-only → assisted intake → assisted verify → supervised (certification finalization always human; student intake is the ONLY auto-eligible op, no direct capital risk) |
| `src/cultural/operation.cljc` | **OperationActor** -- langgraph-clj StateGraph |
| `src/cultural/sim.cljc` | demo driver |
| `test/cultural/*_test.clj` | governor contract · phase invariants · store parity · registry conformance · facts coverage |

## Business-process coverage (honest)

This actor covers student intake through cultural-education/child-
performer regulatory assessment, child-performer-permit screening and
certification/progress-record finalization -- the core governed
lifecycle this blueprint's own `docs/business-model.md` names as its
Offer:

| Covered | Not covered (out of scope for this R0) |
|---|---|
| Student intake + per-jurisdiction evidence checklisting, HARD-gated on an official spec-basis citation (`:student/intake`/`:curriculum/verify`) | Real school-management-system integration, real music/arts/dance/language instruction itself (see `cultural.facts`'s docstring) |
| Child-performer-permit screening, evaluated unconditionally so the screening op itself can HARD-hold on its own finding (`:permit/screen`) | Any artistic-quality judgment itself -- deliberately outside this actor's competence |
| Certification/progress-record finalization, HARD-gated on full evidence, sufficient practice hours and a resolved child-performer-permit status, plus a double-finalization guard (`:actuation/finalize-certification`) | |
| Immutable audit ledger for every intake/verification/screening/finalization decision | |

Extending coverage is additive: add the next gate (e.g. a recital-
recording-consent check) as its own governed op with its own HARD
checks and tests, following the SAME "an independent governor
re-verifies against the actor's own records before any real-world
act" pattern this repo's flagship op already establishes.

## Jurisdiction coverage (honest)

`cultural.facts/coverage` reports how many requested jurisdictions
actually have an official spec-basis in `cultural.facts/catalog` --
currently 4 seeded (JPN, USA, GBR, DEU) out of ~194 jurisdictions
worldwide. This is a starting catalog to prove the governor contract
end-to-end, not a claim of global coverage. Adding a jurisdiction is
additive: one map entry in `cultural.facts/catalog`, citing a real
official source -- never fabricate a jurisdiction's requirements to
make coverage look bigger.

## Maturity

`:implemented` -- `StudioEdOps-LLM` + `Instruction Integrity
Governor` run as real, tested code (see `Run` above), promoted from
the originally-published `:blueprint`-tier scaffold, modeled closely
on the sixty-seven prior actors' architecture. See `docs/adr/0001-
architecture.md` for the history and design.

## License

Code and implementation templates are AGPL-3.0-or-later.
