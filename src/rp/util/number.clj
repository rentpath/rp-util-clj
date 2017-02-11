(ns rp.util.number)

(defn nat-num?
  [x]
  (and (number? x)
       (not (neg? x))))

(defn longitude?
  [x]
  (and (number? x)
       (<= -180 x 180)))

(defn latitude?
  [x]
  (and (number? x)
       (<= -90 x 90)))
