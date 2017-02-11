(ns rp.util.string-spec
  (:require [rp.util.string :as util-string]
            [rp.util.number :as util-number]
            [clojure.spec :as s]
            [clojure.spec.gen :as g]))

(def spec-invalid ::s/invalid)

(defn truthy->conform-pred
  [f]
  (fn [x]
    (if-some [result (f x)]
      result
      spec-invalid)))

(s/def ::boolean (s/spec (s/conformer (truthy->conform-pred util-string/parse-boolean)
                                      str)
                         :gen (fn []
                                (g/bind (s/gen boolean?)
                                        #(g/return (str %))))))
(s/def ::long (s/spec (s/conformer (truthy->conform-pred util-string/parse-long)
                                   str)
                      :gen (fn []
                             (g/bind (s/gen int?)
                                     #(g/return (str %))))))
(s/def ::double (s/spec (s/conformer (truthy->conform-pred util-string/parse-double)
                                     str)
                        :gen (fn []
                               (g/bind (s/gen double?)
                                       #(g/return (str %))))))
(s/def ::nat-long (s/and ::long util-number/nat-num?))
(s/def ::nat-double (s/and ::double util-number/nat-num?))
(s/def ::bounding-box (s/spec (s/conformer (truthy->conform-pred util-string/parse-bounding-box)
                                           str)
                              :gen (fn []
                                     (g/bind (s/gen (s/tuple (s/and double? util-number/longitude?)
                                                             (s/and double? util-number/latitude?)
                                                             (s/and double? util-number/longitude?)
                                                             (s/and double? util-number/latitude?)))
                                             #(g/return (apply str (interpose "," %)))))))
