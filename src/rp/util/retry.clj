(ns rp.util.retry)

(defn with-retries
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
  [f & {:keys [max-attempts attempt delay max-delay exceptions predicate error-fn failure-fn]
        :or {max-attempts 5
             attempt 1
             delay 1000
             exceptions [Exception]
             predicate boolean
             error-fn identity
             failure-fn identity}}]
  (let [max-delay (or max-delay
                      (Math/abs (* delay max-attempts)))
        infinite? (< max-attempts 0)]
    (loop [attempt 1]
      (let [sleep-time (min max-delay (* delay attempt))
            result (try
                     (f)
                     (catch Exception e
                       (error-fn e)
                       (let [retry? (and (or (> max-attempts attempt)
                                             infinite?)
                                         (some #(instance? % e) exceptions)
                                         (predicate e))]
                         (when (or (not retry?)
                                   (and infinite?
                                        (>= sleep-time max-delay)))
                           (failure-fn e))
                         (if retry?
                           ::retry
                           (throw e)))))]
        (if (= result ::retry)
          (do
            (Thread/sleep sleep-time)
            (recur (inc attempt)))
          result)))))
