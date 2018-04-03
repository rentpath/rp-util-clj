(ns rp.util.retry-test
  (:require [clojure.test :refer :all]
            [rp.util.retry :refer :all]))

(defn build-collector []
  (atom {:counter      0
         :periods      []
         :last-attempt (System/currentTimeMillis)}))

(defn build-testfn [collector]
  (fn []
    (swap! collector
           (fn [{:keys [last-attempt counter] :as c}]
             (let [now (System/currentTimeMillis)]
               (-> (if (> counter 0)
                     (update c :periods conj (- now last-attempt))
                     c)
                   (update :counter inc)
                   (assoc :last-attempt now)))))
    (throw (Exception. "Generic Exception"))))

(defn test-collector [collector & {:keys [expected-attempts expected-delay timing-error-allowance]
                                   :or   {expected-attempts      5
                                          expected-delay         1000
                                          timing-error-allowance 20}}]
  (let [{:keys [periods counter]} @collector]
    (is (= expected-attempts counter))
    (doseq [i (range (dec expected-attempts))]
      (is (< (Math/abs (- (nth periods i) (* (inc i) expected-delay))) timing-error-allowance)))))

(deftest test-defaults
  (let [collector (build-collector)
        f (build-testfn collector)]
    (try (with-retries f)
         (catch Exception _))
    (test-collector collector)))

(deftest test-max-attempts
  (let [collector (build-collector)
        f (build-testfn collector)]
    (try (with-retries f :max-attempts 3)
         (catch Exception _))
    (test-collector collector :expected-attempts 3)))

(deftest test-delay
  (let [collector (build-collector)
        f (build-testfn collector)]
    (try (with-retries f :delay 500)
         (catch Exception _))
    (test-collector collector :expected-delay 500)))

(deftest test-error-fn
  (let [collector (build-collector)
        f (build-testfn collector)
        error-fn-call-count (atom 0)
        error-fn (fn [^Exception e]
                  (swap! error-fn-call-count inc)
                  (is (= (.getMessage e) "Generic Exception"))
                  (is (instance? Exception e)))]
    (try (with-retries f :error-fn error-fn)
         (catch Exception _))
    (is (= 5 @error-fn-call-count))))

(deftest test-failure-fn
  (let [collector (build-collector)
        f (build-testfn collector)
        failure-fn-call-count (atom 0)
        failure-fn (fn [^Exception e]
                   (swap! failure-fn-call-count inc)
                   (is (= (.getMessage e) "Generic Exception"))
                   (is (instance? Exception e)))]
    (try (with-retries f :failure-fn failure-fn)
         (catch Exception _))
    (is (= 1 @failure-fn-call-count))))
