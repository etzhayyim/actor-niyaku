(ns niyaku.repository-contract-test
  (:require [clojure.edn :as edn] [clojure.test :refer [deftest is]]))
(deftest canonical-files-parse
  (doseq [p ["manifest.edn" "schema.edn" "data/terminal.edn"
             "lex/moveAttestation.edn" "identity.edn" "dependencies.edn"
             "repository-contracts.edn" "migration.edn"]]
    (is (some? (edn/read-string (slurp p))) p)))
(deftest standalone-contract
  (let [c (edn/read-string (slurp "repository-contracts.edn"))]
    (is (= :edn (:canonical-data c)))
    (is (contains? (:deprecated-languages c) :go))))
