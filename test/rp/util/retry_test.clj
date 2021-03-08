(ns rp.util.retry-test
  (:require [clojure.test :refer :all]
            [rp.util.retry :refer :all])
  (:import (java.util.concurrent TimeoutException)))

(defn build-collector []
  (atom {:counter      0
         :periods      []
         :last-attempt (System/currentTimeMillis)}))

(defn update-collector [collector]
  (swap! collector
         (fn [{:keys [last-attempt counter] :as c}]
           (let [now (System/currentTimeMillis)]
             (-> (if (> counter 0)
                   (update c :periods conj (- now last-attempt))
                   c)
                 (update :counter inc)
                 (assoc :last-attempt now))))))

(defn build-testfn [collector]
  (fn []
    (update-collector collector)
    (throw (Exception. "Generic Exception"))))

(defn test-collector [collector & {:keys [expected-attempts expected-delay timing-error-allowance]
                                   :or   {expected-attempts      5
                                          expected-delay         1000
                                          timing-error-allowance 50}}]
  (let [{:keys [periods counter]} @collector]
    (is (= expected-attempts counter))
    (doseq [i (range (dec expected-attempts))]
      (is (< (Math/abs ^long (- (nth periods i) (* (inc i) expected-delay))) timing-error-allowance)))))

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

(deftest test-errorless
  (let [collector (build-collector)
        f #(update-collector collector)]
    (with-retries f)
    (test-collector collector :expected-attempts 1)))

(deftest test-limited-exceptions
  (let [generic-collector (build-collector)
        generic-f (build-testfn generic-collector)
        specific-collector (build-collector)
        specific-f (fn []
                     (update-collector specific-collector)
                     (throw (TimeoutException. "Generic Exception")))]
    (is (thrown? Exception (with-retries generic-f :exceptions [TimeoutException])))
    (try (with-retries specific-f :exceptions [TimeoutException])
         (catch Exception _))
    (test-collector generic-collector :expected-attempts 1)
    (test-collector specific-collector)))