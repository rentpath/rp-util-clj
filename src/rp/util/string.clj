(ns rp.util.string
  (:require [clojure.tools.reader.edn :as edn]
            [clojure.string :as str]))

(def long-regex (re-pattern "([-+]?)(?:(0)|([1-9][0-9]*)|0[xX]([0-9A-Fa-f]+)|0([0-7]+)|([1-9][0-9]?)[rR]([0-9A-Za-z]+)|0[0-9]+)(N)?"))
(def double-regex (re-pattern "([-+]?[0-9]+(\\.[0-9]*)?([eE][-+]?[0-9]+)?)(M)?"))
(def bool-regex (re-pattern "(?i)true|false"))

(defn parseable?
  [re s]
  (first (re-matches re s)))

(defn make-parser
  [regex]
  (fn [s]
    (when (and (string? s) (parseable? regex s))
      (edn/read-string (str/lower-case s)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Public API

(def parse-long (make-parser long-regex))
(def parse-boolean (make-parser bool-regex))
(def parse-double* (make-parser double-regex))

(defn parse-double
  [s]
  (some-> s
          parse-double*
          double))
