(ns rp.util.number-spec
  (:require [rp.util.number :as util-number]
            [clojure.spec :as s]))

(s/def ::nat-long nat-int?)
(s/def ::nat-double (s/and double? util-number/nat-num?))
