(ns rp.util.keyword
  (:require [rp.util.string :as util-string]))

(defn nat-long-keyword?
  [x]
  (and (keyword? x)
       (-> (name x)
           util-string/parse-long
           nat-int?)))
