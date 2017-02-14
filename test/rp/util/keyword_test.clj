(ns rp.util.keyword-test
  (:require [clojure.test :refer :all]
            [rp.util.keyword :refer :all]))

(deftest test-nat-long-keyword?
  (is (nat-long-keyword? :0))
  (is (nat-long-keyword? :13))
  (is (not (nat-long-keyword? :1.0)))
  (is (not (nat-long-keyword? :-1)))
  (is (not (nat-long-keyword? :alice)))
  (is (not (nat-long-keyword? "Bob")))
  (is (not (nat-long-keyword? 47)))
  (is (not (nat-long-keyword? nil))))
