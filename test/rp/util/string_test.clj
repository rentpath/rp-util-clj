(ns rp.util.string-test
  (:require [clojure.test :refer :all]
            [rp.util.string :refer :all]))

(deftest test-parse-long
  (are [s x] (= x (parse-long s))
    "35" 35
    "532435345" 532435345
    "a" nil
    "463f" nil
    nil nil
    "" nil))

(deftest test-parse-double
  (are [s x] (= x (parse-double s))
    "0.0" 0.0
    "14.435435" 14.435435
    "35.0" 35.0
    "35" 35.0
    "532435345" 5.32435345E8
    "a" nil
    "463f" nil
    nil nil
    "" nil))

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
    "463f" nil
    nil nil
    "" nil))

(deftest test-non-blank-string
  (are [x result] (= result (non-blank-string x))
    "test" "test"
    "13" "13"
    "" nil
    13 nil
    nil nil))

(deftest test-split-on-re
  (are [x re result] (= result (#'rp.util.string/split-on-re re x))
    nil (:caret regexes) []
    13 (:comma regexes) []
    "" (:caret regexes) []
    "," (:comma regexes) []
    "^" (:caret regexes) []
    "foobar" (:caret regexes) ["foobar"]
    "foo^bar" (:caret regexes) ["foo" "bar"]
    "^foo^bar^" (:caret regexes) ["" "foo" "bar"]
    " ^ foo ^ bar ^" (:caret regexes) ["" "foo" "bar"]
    "fred,bill,bob" (:comma regexes) ["fred" "bill" "bob"]))

(deftest test-split-on-bats-and-carets
  (are [x result] (= result (split-on-bats-and-carets x))
    nil []
    "" []
    "foobar" [["foobar"]]
    "foobar^+^barfoo" [["foobar"] ["barfoo"]]
    "alpha^1^+^beta^2" [["alpha" "1"] ["beta" "2"]]
    "^^+^bar^^+^" [[] ["bar"]]))

(deftest test-carets->map
  (let [ks [:x :y]]
    (are [x result] (= result (carets->map ks x))
      nil {}
      "" {}
      ;; A value for each key (normal case)
      "a^b" {:x "a" :y "b"}
      ;; Extra values are ignored
      "a^b^c" {:x "a" :y "b"}
      ;; Keys without a value are discarded
      "a" {:x "a"}
      "^b" {:y "b"}
      " ^ b" {:y "b"})))

(deftest test-bats-and-carets->maps
  (let [ks [:x :y]]
    (are [x result] (= result (bats-and-carets->maps ks x))
      nil []
      "" []
      ;; A value for each key (normal case)
      "a^b^+^c^d" [{:x "a" :y "b"}
                   {:x "c" :y "d"}]
      ;; Extra values are ignored
      "a^b^+^c^d^e" [{:x "a" :y "b"}
                     {:x "c" :y "d"}]
      ;; Keys without a value are discarded
      "a^b^+^c" [{:x "a" :y "b"}
                 {:x "c"}]
      "^b^+^^d" [{:y "b"}
                 {:y "d"}])))
