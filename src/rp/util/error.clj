(ns rp.util.error
  (:require [clojure.tools.logging :as log]
            [rp.error.reporter :as reporter]))

(defn report-throwable!
  "Report a throwable with optional metadata map."
  ([error-reporter t meta]
   (when error-reporter
     (try
       (reporter/report-throwable! error-reporter t {:meta meta})
       (catch Throwable t2
         (log/error t2 "Error trying to call rp.error.reporter/report-throwable!")))))
  ([error-reporter t]
   (report-throwable! error-reporter t nil)))

(defn handle-thread-exception
  "Error function for :rp.syringe/handle-thread-exception"
  [{:keys [error-reporter] :as sys} ^Thread thread throwable]
  (report-throwable! error-reporter
                     throwable
                     {:message "Caught exception from a thread"
                      :thread (.getName thread)}))
