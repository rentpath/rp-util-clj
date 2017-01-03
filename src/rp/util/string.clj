(ns rp.util.string
  (:require [clojure.tools.reader.edn :as edn]
            [clojure.string :as str]))

(def regexes
  {:long (re-pattern "([-+]?)(?:(0)|([1-9][0-9]*)|0[xX]([0-9A-Fa-f]+)|0([0-7]+)|([1-9][0-9]?)[rR]([0-9A-Za-z]+)|0[0-9]+)(N)?")
   :double (re-pattern "([-+]?[0-9]+(\\.[0-9]*)?([eE][-+]?[0-9]+)?)(M)?")
   :boolean (re-pattern "(?i)true|false")
   :comma #","
   :caret #"\^"
   :bat #"\^\+\^"})

(declare non-blank-string)

(defn- parseable
  [re s]
  (when (non-blank-string s)
    (first (re-matches re s))))

(defn- make-parser
  [re]
  (fn [s]
    (when (parseable re s)
      (try
        (edn/read-string s)
        (catch Exception e)))))

(defn- string
  [s]
  (when (string? s)
    s))

(defn- non-blank
  [s]
  (when-not (str/blank? s)
    s))

(defn- lower-case
  [s]
  (when s
    (str/lower-case s)))

(defn- split-on-re
  [re s]
  (if (non-blank-string s)
    (mapv str/trim (str/split s re))
    []))

(def parse-double* (make-parser (:double regexes)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Public API
(def non-blank-string (comp non-blank string))
(def parse-long (make-parser (:long regexes)))
(def parse-boolean (comp (make-parser (:boolean regexes))
                         lower-case))

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

(def split-on-comma (partial split-on-re (:comma regexes)))
(def split-on-caret (partial split-on-re (:caret regexes)))
(def split-on-bat (partial split-on-re (:bat regexes)))

(defn split-on-bats-and-carets
  [s]
  (mapv split-on-caret (split-on-bat s)))

(defn carets->map
  [ks s]
  (zipmap ks (split-on-caret s)))

(defn bats-and-carets->maps
  [ks s]
  (mapv #(carets->map ks %) (split-on-bat s)))
