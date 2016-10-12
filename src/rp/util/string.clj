(ns rp.util.string
  (:require [clojure.tools.reader.edn :as edn]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Public API

(defn parse-boolean
  [s]
  (edn/read-string s))

(defn parse-double
  [s]
  (edn/read-string s))

(defn parse-long
  [s]
  (edn/read-string s))
