(ns rp.util.map-test
  (:require [clojure.test :refer :all]
            [rp.util.map :refer :all]))

(deftest test-select-keys-by
  (let [m {:john 1
           :paul 2
           :george 3
           :ringo 4}
        active #{:paul :ringo}
        active-m {:paul 2 :ringo 4}]
    (is (= active-m
           (select-keys-by active m)))))

(deftest test-select-vals-by
  (let [m {:john 1
           :paul 2
           :george 3
           :ringo 4}
        greater-than-2 (partial < 2)
        greater-than-2-m {:george 3 :ringo 4}]
    (is (= greater-than-2-m
           (select-vals-by greater-than-2 m)))))

(deftest test-relocate-keys-by
  (let [m {:john 1
           :paul 2
           :george 3
           :ringo 4}
        active #{:paul :ringo}
        relocated-by-active {:john 1
                             :george 3
                             :active {:paul 2
                                      :ringo 4}}]
    (is (= relocated-by-active
           (relocate-keys-by active :active m)))))

(deftest test-relocate-vals-by
  (let [m {:john 1
           :paul 2
           :george 3
           :ringo 4}
        greater-than-2 (partial < 2)
        relocated-by-greater-than-2 {:john 1
                                     :paul 2
                                     :greater-than-2 {:george 3
                                                      :ringo 4}}]
    (is (= relocated-by-greater-than-2
           (relocate-vals-by greater-than-2 :greater-than-2 m)))))
