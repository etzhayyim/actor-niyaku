#!/usr/bin/env bb
;; niyaku 荷役 — bb-native test runner (Clojure / babashka; no shell). ADR-2606072802.
;; Per the repo-wide rule (root CLAUDE.md §"Operational code = clj/bb"): first-party
;; tooling is clj/bb, NOT shell. New actors ship run_tests.clj, not run_tests.sh; this
;; replaces the former run_tests.sh.
;;
;;   bb run_tests.clj      ; run from the standalone repository root
;;
;; methods/isaac_sway_sim.cljc's resolve-py-src captured *file* lazily inside its own
;; function body — only reliably bound during the file's own top-level compilation, the
;; same bug class fixed for himotoki/keizu this session. Fixed by capturing it once in a
;; top-level def (this-file) instead.
(require '[babashka.classpath :as cp]
         '[babashka.fs :as fs]
         '[clojure.test :as t])

(cp/add-classpath (str (fs/path (fs/parent (fs/absolutize *file*)) "src")))
(cp/add-classpath (str (fs/path (fs/parent (fs/absolutize *file*)) "test")))

(def suites '[niyaku.cells.test-state-machine
              niyaku.methods.test-agv-transfer
              niyaku.methods.test-crane-dynamics
              niyaku.methods.test-isaac-sway-sim
              niyaku.methods.test-stow-plan
              niyaku.methods.test-terminal-cycle
              niyaku.repository-contract-test])

(apply require suites)

(let [{:keys [fail error]} (apply t/run-tests suites)]
  (System/exit (if (zero? (+ fail error)) 0 1)))
