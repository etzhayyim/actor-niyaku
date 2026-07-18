# niyaku 荷役 — CLAUDE guidance

Automated port cargo handling (ship↔shore container loading/unloading). Tier-B actor,
ADR-2606082000, R0 scaffold. Operator-side counterpart of **funadaiku** (builds the ships).

## What this actor is

The port-side automation the roster lacked: funadaiku *builds* the cargo ship, niyaku
*loads and unloads* it. The technical core is **anti-sway control** of a suspended
container, modelled as a cart + hanging load — the Cartpole topology — and verified
through the clean-room `isaacsim.core.api` (`kotodama.nv_compat`, ADR-2605261800).

## Layout

- `src/niyaku/methods/` — canonical CLJC computation modules.
- `src/niyaku/cells/` — nine canonical CLJC state machines.
- `test/niyaku/` — standalone Clojure test suites.
- `data/terminal.edn` — reference terminal + STS crane + sample vessel cell (illustrative).
- `lex/moveAttestation.edn` — per-move container-handling attestation lexicon.

## Hard rules (per ADR-2606082000 gates)

- **G2 clean-room only.** The Isaac integration mirrors the *public* `isaacsim.core.api`
  call shapes via `kotodama.nv_compat`. NEVER link/import any NVIDIA Isaac Sim binary,
  header, or library. The dynamics are KAMI-native.
- **G8 zero-emission.** Electric cranes/AGVs only; no diesel RTG. Regenerative
  hoist-lowering energy is credited in `emissions_audit`.
- **G9 stow feasibility.** `stow_plan` must enforce weight-on-top, port-rotation
  (no re-handle), reefer-row, and IMDG hazmat segregation. Do not relax these to "make a
  plan fit" — an infeasible request must raise `StowError`.
- **G10 no weapons cargo.** Weapons-transport / military-materiel handling is N-excluded
  (Charter Rider §2(a)). Cargo provenance is gated.
- **G11 / G14 no worker surveillance.** Productivity is `moves/hour` (equipment KPI), never
  a per-longshoreman pace ranking. No worker biometric/pace tracking.
- **G12 no-server-key / consent-bound.** Methods are pure compute and move no real crane;
  R0 stops at "intent". Real actuation is Council-gated R1 (ADR-2606082015, reserved).

## Running tests

Run `bb test` from the repository root. Python parity oracles are retired; CLJC
is the canonical implementation and the suite is offline and deterministic.

## Anti-sway sign conventions (don't flip these without re-deriving)

- **`crane_dynamics.GantryCrane`**: equilibrium θ=0 (load hangs down); trolley accel
  couples as `-a/L` into θ̈. Sway feedback is **positive** (`+k_theta·θ`) — it stiffens the
  restoring term.
- **`isaac_sway_sim` (Cartpole)**: equilibrium θ=π (hanging); a +force drives φ=θ−π
  **positive**. Sway feedback is **negative** (`-k_phi·φ`). The two models have opposite
  trolley-accel→sway coupling sign, hence opposite feedback sign. Both are verified by test.
