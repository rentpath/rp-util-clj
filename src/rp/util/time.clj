(ns rp.util.time
  (:import [java.time LocalTime ZoneId LocalDateTime ZonedDateTime Instant]
           [java.time.format DateTimeFormatterBuilder DateTimeFormatter]
           [java.time.temporal ChronoField]
           [java.util Locale]
           [java.text ParsePosition]))

(def default-zone-id (ZoneId/of "America/New_York"))

;; Some common patterns for parse-instant
(def patterns
  ["dd-MMM-yyyy"
   "MM/dd/yyyy"
   "MM/dd/yyyy HH:mm:ss"
   "yyyyMMddHHmmss.SSSSSS"])

(defn formatter
  "Returns case insensitive DateTimeFormatter built
   from date format string s"
  ([s]
   (formatter s LocalTime/NOON))
  ([s ^LocalTime default-time]
   (let [hour (.getHour default-time)
         minute (.getMinute default-time)
         second (.getSecond default-time)]
     (.. (DateTimeFormatterBuilder.)
         parseCaseInsensitive
         (appendPattern s)
         (parseDefaulting ChronoField/HOUR_OF_DAY hour)
         (parseDefaulting ChronoField/MINUTE_OF_HOUR minute)
         (parseDefaulting ChronoField/SECOND_OF_MINUTE second)
         (toFormatter (Locale/getDefault))))))

(def formatters
  (mapv formatter patterns))

(defn local-time->instant
  ([s formatter]
   (local-time->instant s formatter default-zone-id))
  ([s formatter zone-id]
   (try
     (let [local-date-time (LocalDateTime/parse s formatter)
           zoned-date-time (ZonedDateTime/of local-date-time zone-id)]
       (.toInstant ^ZonedDateTime zoned-date-time))
     (catch Throwable t
       nil))))

(defn parse-iso-instant
  [s]
  (try
    (Instant/parse s)
    (catch Throwable t
      nil)))

(defn parse-instant
  [s]
  (or (first (keep #(local-time->instant s %) formatters))
      (parse-iso-instant s)))
