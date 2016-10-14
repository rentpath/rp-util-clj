(ns rp.util.logic-test
  (:require [clojure.test :refer :all]
            [rp.util.logic :refer :all]))

(deftest test-only-one
  (are [xs x] (is (= x (apply only-one xs)))
    [false true false] true
    [13 nil false] 13
    [nil "bob" nil] "bob"
    [false ["christmas"] false] ["christmas"]
    [[:test]] [:test]
    [:test :christmas] nil
    [nil false nil] nil
    [false false false] nil
    [true true false] nil))
