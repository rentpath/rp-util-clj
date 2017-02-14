(ns rp.util.number-test
  (:require [clojure.test :refer :all]
            [rp.util.number :refer :all]))

(deftest test-nat-num?
  (is (nat-num? 0))
  (is (nat-num? 0.0))
  (is (nat-num? 3))
  (is (nat-num? 3.0))
  (is (not (nat-num? -1)))
  (is (not (nat-num? -0.1)))
  (is (not (nat-num? nil))))

(deftest test-longitude?
  (is (longitude? 0))
  (is (longitude? 180))
  (is (longitude? -180))
  (is (longitude? 0.0))
  (is (longitude? 180.0))
  (is (longitude? -180.0))
  (is (not (longitude? 181)))
  (is (not (longitude? -181)))
  (is (not (longitude? 180.1)))
  (is (not (longitude? -180.1)))
  (is (not (longitude? nil))))

(deftest test-latitude?
  (is (latitude? 0))
  (is (latitude? 90))
  (is (latitude? -90))
  (is (latitude? 0.0))
  (is (latitude? 90.0))
  (is (latitude? -90.0))
  (is (not (latitude? 91)))
  (is (not (latitude? -91)))
  (is (not (latitude? 90.1)))
  (is (not (latitude? -90.1)))
  (is (not (latitude? nil))))

(deftest test-within-bounding-box?
  (is (within-bounding-box? [3 1 0 5] [2 4]))
  (is (within-bounding-box? [0 5 3 1] [2 4]))
  (is (not (within-bounding-box? [3 1 0 5] [4 0])))
  (is (not (within-bounding-box? [0 5 3 1] [4 0])))
  (is (not (within-bounding-box? [:alice :bob :alice :bob] [:alice :bob])))
  (is (not (within-bounding-box? ["Alice" "Bob" "Alice" "Bob"] ["Alice" "Bob"])))
  (is (not (within-bounding-box? nil nil)))
  (is (not (within-bounding-box? [nil nil nil nil] [nil nil]))))
