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
           (relocate-keys-by active [:active] m)))))

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
           (relocate-vals-by greater-than-2 [:greater-than-2] m)))))

(deftest test-dissoc-in
  (is (= (dissoc-in {:a {:b {:c 1 :d 2}}} [:a :b :c])
         {:a {:b {:d 2}}}))
  (is (= (dissoc-in {:a {:b {:c 1}}} [:a :b :c])
         {}))
  (is (= (dissoc-in {:a {:b {:c 1} :d 2}} [:a :b :c])
         {:a {:d 2}}))
  (is (= (dissoc-in {:a 1} [])
         {:a 1})))

(deftest test-relocate-keys
  (let [m {:homer-parents {:abe :male
                           :mona :female}
           :flanders {:ned :male
                      :maude :female
                      :todd :male
                      :rod :male}
           :simpsons {:marge :female
                      :homer :male
                      :maggie :female
                      :bart :male
                      :lisa :female}}
        relocated {:flanders {:ned :male
                              :maude :female
                              :todd :male
                              :rod :male}
                   :simpsons {:marge :female
                              :homer :male
                              :maggie :female
                              :bart :male
                              :lisa :female
                              :parents {:homer {:abe :male
                                                :mona :female}}}}]
    (is (= relocated
           (relocate-keys m [:homer-parents] [:simpsons :parents :homer]))))

  (is (= {:range {:low 3}}
         (relocate-keys {:range {:low 3 :high nil}}
                        [:range :high]
                        [:some :where :else]))
      "Original keys should always be removed, even when present with a nil value."))

(deftest test-ensure-keys
  (is (= {:a nil :b "foo" :c nil :d "bar"}
         (ensure-keys [:a :b :c] {:b "foo" :d "bar"}))))
