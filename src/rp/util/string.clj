(ns rp.util.string
  (:require [clojure.tools.reader.edn :as edn]
            [clojure.string :as str]))

(def ^:const long-regex (re-pattern "([-+]?)(?:(0)|([1-9][0-9]*)|0[xX]([0-9A-Fa-f]+)|0([0-7]+)|([1-9][0-9]?)[rR]([0-9A-Za-z]+)|0[0-9]+)(N)?"))
(def ^:const double-regex (re-pattern "([-+]?[0-9]+(\\.[0-9]*)?([eE][-+]?[0-9]+)?)(M)?"))
(def ^:const bool-regex (re-pattern "(?i)true|false"))

(def ^:const comma-regex #",")

(defn parseable?
  [re s]
  (first (re-matches re s)))

(defn make-parser
  [regex]
  (fn [s]
    (when (and (string? s) (parseable? regex s))
      (try
        (edn/read-string s)
        (catch Exception e)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Public API

(def parse-long (make-parser long-regex))
(def parse-boolean (comp (make-parser bool-regex)
                         #(when % (str/lower-case %))))
(def parse-double* (make-parser double-regex))

(defn parse-double
  [s]
  (some-> s
          parse-double*
          double))

(defn parse-big-decimal
  [s]
  (some-> s
          parse-double*
          bigdec))

(defn non-blank-string
  "Returns x if it is a non-blank string, nil otherwise."
  [x]
  (when (and (string? x)
             (not (str/blank? x)))
    x))
