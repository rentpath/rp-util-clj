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

(defn within-bounding-box?
  [[lng1 lat1 lng2 lat2 :as bounding-box]
   [longitude latitude :as coordinates]]
  (and (every? number? (into bounding-box coordinates))
       (<= lng1 longitude lng2)
       (<= lat1 latitude lat2)))
