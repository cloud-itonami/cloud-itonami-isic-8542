# Business Model: Cultural education

## Classification

- Repository: `cloud-itonami-isic-8542`
- ISIC Rev.5: `8542`
- Activity: cultural education -- instruction in music, arts, dance, language and other cultural skills
- Social impact: education access, data sovereignty, transparent audit

## Customer

- independent arts/music schools
- cooperative cultural-education collectives
- community cultural programs

## Offer

- student enrollment intake
- curriculum/program proposal
- certification/progress proposal
- immutable audit ledger

## Revenue

- self-host setup: one-time implementation fee
- managed hosting: monthly subscription per school
- support: monthly retainer with SLA
- migration: import from an incumbent school-management system
- per-enrollment fee

## Trust Controls

- no certification or progress record is finalized without human sign-off
- a fabricated assessment forces a hold, not an override
- every record path is auditable
- student data stays outside Git
- emergency manual override paths remain outside LLM control
- an unresolved child-performer work permit, or insufficient practice
  hours, forces a hold, not an override
- certification finalization is logged and escalated, and cannot be
  finalized twice for the same student: a double-finalization attempt
  is held off this actor's own student facts alone, with no upstream
  comparison needed

## Instruction Integrity Governor: decision rule

`blueprint.edn` fixes `:itonami.blueprint/governor` to `:instruction-
integrity-governor` -- this is not a generic "review step," it is the
one gate the ONE real-world act this business performs (finalizing a
real certification or progress record) must pass. The governor sits
between the StudioEdOps-LLM and execution, per the README's Core
Contract:

```text
StudioEdOps-LLM -> Instruction Integrity Governor -> hold, proceed, or human approval
```

**Approves**: routine cultural-education actions proposed against a
student that already has a consented curriculum on file, satisfied
required evidence, sufficient recorded practice hours, and a
resolved child-performer-permit status. These proceed straight to
the student ledger.

**Rejects or escalates**: the governor refuses to let the advisor
finalize a certification on its own authority when any of the
following hold -- a fabricated jurisdiction spec-basis; incomplete
evidence; insufficient practice hours; an unresolved child-performer
work permit; a double-finalization attempt. A clean finalization
proposal still always routes to a human -- `:actuation/finalize-
certification` is never auto-committed, at any rollout phase.
