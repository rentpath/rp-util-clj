(ns rp.util.logic-test
  (:require [clojure.test :refer :all]
            [clojure.spec :as s]
            [clojure.spec.test :as stest]
            [rp.util.logic :refer :all]))

(s/fdef rp.util.logic/xor
        :args (s/cat :p boolean? :q boolean?)
        :ret boolean?
        :fn (fn [{{:keys [p q]} :args
                  ret :ret}]
              (let [truth-table {[false false] false
                                 [false  true] true
                                 [true  false] true
                                 [true   true] false}]
                (= ret (get truth-table [p q])))))

(deftest test-xor
  (let [[{:keys [failure]}] (stest/check 'rp.util.logic/xor)]
    (is (not failure))))
