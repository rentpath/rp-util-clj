(ns rp.util.logic)

(defn only-one
  "Returns x in xs if x is the only truthy x, nil otherwise."
  [& xs]
  (when-let [[x :as truthies] (filter identity xs)]
    (when (= 1 (count truthies))
      x)))
