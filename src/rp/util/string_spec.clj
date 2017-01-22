(ns rp.util.string-spec
  (:require [rp.util.string :as util-string]
            [rp.util.number :as util-number]
            [clojure.spec :as s]
            [clojure.spec.gen :as g]))

(def invalid ::s/invalid)

(defn conformer
  [f]
  (s/conformer (fn [x]
                 (if-let [result (f x)]
                   result
                   invalid))
               str))

(defn generator
  [gen]
  (fn []
    (g/bind gen
            #(g/return (str %)))))

(s/def ::long (s/spec (conformer util-string/parse-long)
                      :gen (generator (s/gen int?))))
(s/def ::double (s/spec (conformer util-string/parse-double)
                        :gen (generator (s/gen double?))))
(s/def ::nat-long (s/and ::long util-number/nat-num?))
(s/def ::nat-double (s/and ::double util-number/nat-num?))
