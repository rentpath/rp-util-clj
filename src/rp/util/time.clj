(ns rp.util.time
  (:import [java.time LocalTime ZoneId LocalDateTime ZonedDateTime Instant]
           [java.time.format DateTimeFormatterBuilder DateTimeFormatter]
           [java.time.temporal ChronoField]
           [java.util Locale]
           [java.text ParsePosition]))

(def default-zone-id (ZoneId/of "America/New_York"))

(def patterns
  {:review      "dd-MMM-yyyy"
   :floorplan   "MM/dd/yyyy"
   :last-update "MM/dd/yyyy HH:mm:ss"})

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
  (merge (reduce-kv (fn [m k v]
                      (assoc m k (formatter v)))
                    {}
                    patterns)
         {:iso-instant DateTimeFormatter/ISO_INSTANT}))

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

(defn parses
  [s [type ^DateTimeFormatter formatter]]
  (when s
    (let [parse-position (ParsePosition. 0)
          unresolved-parse (.parseUnresolved formatter s parse-position)
          parsed-all-chars? (= (count s) (.getIndex parse-position))]
      (when (and unresolved-parse parsed-all-chars?)
        type))))

(defmulti parse-instant (fn [s]
                          (when-let [[pattern-type] (keep #(parses s %) formatters)]
                            pattern-type)))
(defmethod parse-instant :default [s] nil)
(defmethod parse-instant :review [s]
  (local-time->instant s (:review formatters)))
(defmethod parse-instant :floorplan [s]
  (local-time->instant s (:floorplan formatters)))
(defmethod parse-instant :last-update [s]
  (local-time->instant s (:last-update formatters)))
(defmethod parse-instant :iso-instant [s]
  (Instant/parse s))
