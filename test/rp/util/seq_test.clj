(ns rp.util.seq-test
  (:require [clojure.test :refer :all]
            [rp.util.seq :refer :all]))

(deftest test-tuples->maps
  (let [ks [:x :y]]
    (are [x result] (= result (tuples->maps ks x))
      [[1 2] [3 4]] [{:x 1 :y 2}
                     {:x 3 :y 4}]
      [[1] [2 3 4]] [{:x 1}
                     {:x 2 :y 3}])))
