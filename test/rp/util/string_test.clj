(ns rp.util.string-test
  (:require [clojure.test :refer :all]
            [rp.util.string :refer :all]
            [clojure.spec :as s]))

(deftest test-parse-long
  (are [s x] (= x (parse-long s))
    "35" 35
    "532435345" 532435345
    "a" nil
    "463f" nil))

(deftest test-parse-double
  (are [s x] (= x (parse-double s))
    "0.0" 0.0
    "14.435435" 14.435435
    "35.0" 35.0
    "35" 35.0
    "532435345" 5.32435345E8
    "a" nil
    "463f" nil))

(deftest test-parse-boolean
  (are [s x] (= x (parse-boolean s))
    "true" true
    "True" true
    "trUe" true
    "TRUE" true
    "false" false
    "False" false
    "fAlse" false
    "falsey" nil
    "truth" nil
    "0.0" nil
    "14.435435" nil
    "35.0" nil
    "35" nil
    "532435345" nil
    "a" nil
    "463f" nil))

(deftest test-non-blank-string
  (are [s x] (= x (non-blank-string s))
    "test" "test"
    "13" "13"
    "" nil
    13 nil))
