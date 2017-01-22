(ns rp.util.number)

(defn nat-num?
  [x]
  (and (number? x)
       (not (neg? x))))
