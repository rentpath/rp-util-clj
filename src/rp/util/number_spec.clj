(ns rp.util.number-spec
  (:require [rp.util.number :as util-number]
            [clojure.spec.alpha :as spec]))

(spec/def ::nat-long nat-int?)
(spec/def ::nat-double (spec/and double? util-number/nat-num?))
