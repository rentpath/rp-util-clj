(ns rp.util.retry
  (:require [rp.util.notifier :as notifier]
            [clojure.tools.logging :as log]))

(defn- pow [x n]
  "Return x raised to the nth power."
  (reduce * (repeat n x)))

(defn retry
  "This will retry f (a 0-arity fn) until it succeeds (no exception thrown) or encounters a non-retryable exception or has reached the max-retries value (in which case the exception will be thrown).
  Options:
  - backoff-ms (required) specifies the base time to sleep after the first failure; will increase either exponentially or linearly depending on the mode setting for each retry (plus a random amount for some fuzziness if jitter? is specified true)
  - backoff-base (required when mode is :exponential) specifies the base for exponential backoff
  - max-backoff (optional) specifies the maximum backoff (if any). Defaults to -1, indicating no limit
  - mode (optional) either :exponential or :linear to dictate the backoff strategy. Defaults to :exponential
  - max-retries (required) caps the number of retries
  - retryable? (required) predicate that takes an exception/throwable and returns boolean indicating whether to retry; not all exceptions are worth retrying
  - notifier (optional) will receive notifications about retries
  - jitter? (optional) when true adds a random offset to the delay for fuzziness purposes. Defaults to true
  Note: Sleep time before the final retry in exponential mode will be approximately
  `(* backoff-ms (pow backoff-base (dec max-retries)))`
  In lear mode
  `(* backoff-ms max-retries)`"
  [f {:keys [backoff-ms backoff-base max-backoff mode max-retries retryable? notifier jitter?]
      :or   {backoff-ms   nil
             backoff-base nil
             max-backoff  nil
             mode         :exponential
             max-retries  nil
             retryable?   nil
             notifier     nil
             jitter?      true}}]
  (assert (and backoff-ms (or (= mode :linear) (and (= mode :exponential) backoff-base)) max-retries retryable?))
  (loop [try-num 1]
    (let [result (try (f)
                      (catch Throwable e
                        (cond
                          (not (retryable? e))
                          (do
                            (log/error e "Not retryable.")
                            (notifier/maybe-notify! notifier :non-retryable-error {:exception e})
                            (throw e))

                          (> try-num max-retries)
                          (do
                            (log/error e (format "Failed on final try #%s; giving up." try-num))
                            (notifier/maybe-notify! notifier :retries-exhausted {:exception e :try-num try-num})
                            (throw e))

                          :else {::exception e})))]
      (if-let [e (::exception result)]
        (let [sleep-ms (max (case mode
                              :exponential (* backoff-ms (+ (pow backoff-base (dec try-num)) (if jitter? (rand) 0)))
                              :linear (* backoff-ms (+ try-num (if jitter? (rand) 0))))
                            (or max-backoff 0))]
          (log/error e (format "Failed on try #%s and will try again in %s msecs" try-num sleep-ms))
          (notifier/maybe-notify! notifier :retryable-error {:exception e :try-num try-num :sleep-ms sleep-ms})
          (Thread/sleep sleep-ms)
          (recur (inc try-num)))
        result))))

(defn ^{:deprecated    true
        :superseded-by "retry"} with-retries
  "Call the provided function, retrying it up to max-attempts
  if any of the specified exceptions are thrown.
  Options:
  max-attempts - The maximum number of times to call the function. If value is -1, retry infinitely.
  exceptions - a sequence of Exception classes to catch. Default to java.lang.Exception
  delay - The number of milliseconds to wait between attempts, multipled by the attempt count
  max-delay - The longest number of milliseconds to wait between attempts regardless of attempt number.
              Used in conjunction with infinite max-attempts.
  predicate - An optional function that indicates whether to retry. Takes one Exception argument
  error-fn - A function invoked with each exception encountered
  failure-fn - A function invoked when the max attempts or max delay have been reached"
  [f & {:keys [max-attempts delay max-delay exceptions predicate error-fn failure-fn]
        :or   {max-attempts 5
               delay        1000
               exceptions   [Exception]
               predicate    boolean
               error-fn     identity
               failure-fn   identity}}]
  (retry f {:max-retries (dec max-attempts)
            :retryable?  (fn [e] (and (some #(instance? % e) exceptions)
                                      (predicate e)))
            :jitter?     false
            :backoff-ms  delay
            :mode        :linear
            :max-backoff max-delay
            :notifier    (reify notifier/Notifier
                           (notify! [this event {:keys [exception] :as data}]
                             (error-fn exception)
                             (when (= event :retries-exhausted)
                               (failure-fn exception))))}))