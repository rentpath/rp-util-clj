(ns rp.util.string-spec
  (:require [rp.util.string :as util-string]
            [rp.util.number :as util-number]
            [clojure.spec.alpha :as spec]
            [clojure.spec.gen.alpha :as spec-gen]))

(def spec-invalid ::spec/invalid)

(defn truthy->conform-pred
  [f]
  (fn [x]
    (if-some [result (f x)]
      result
      spec-invalid)))

(spec/def ::boolean (spec/spec (spec/conformer (truthy->conform-pred util-string/parse-boolean)
                                               str)
                               :gen (fn []
                                      (spec-gen/bind (spec/gen boolean?)
                                                     #(spec-gen/return (str %))))))
(spec/def ::long (spec/spec (spec/conformer (truthy->conform-pred util-string/parse-long)
                                            str)
                            :gen (fn []
                                   (spec-gen/bind (spec/gen int?)
                                                  #(spec-gen/return (str %))))))
(spec/def ::double (spec/spec (spec/conformer (truthy->conform-pred util-string/parse-double)
                                              str)
                              :gen (fn []
                                     (spec-gen/bind (spec/gen double?)
                                                    #(spec-gen/return (str %))))))
(spec/def ::nat-long (spec/and ::long util-number/nat-num?))
(spec/def ::nat-double (spec/and ::double util-number/nat-num?))
(spec/def ::bounding-box (spec/spec (spec/conformer (truthy->conform-pred util-string/parse-bounding-box)
                                                    str)
                                    :gen (fn []
                                           (spec-gen/bind (spec-gen/tuple (spec-gen/double* {:min -180 :max 180})
                                                                          (spec-gen/double* {:min -90 :max 90})
                                                                          (spec-gen/double* {:min -180 :max 180})
                                                                          (spec-gen/double* {:min -90 :max 90}))
                                                          #(spec-gen/return (apply str (interpose "," %)))))))
