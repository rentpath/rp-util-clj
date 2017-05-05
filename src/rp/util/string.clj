(ns rp.util.string
  (:require [clojure.tools.reader.edn :as edn]
            [clojure.string :as str]
            [rp.util.map :as util-map]
            [rp.util.number :as util-number]))

(def regexes
  {:long (re-pattern "([-+]?)(?:(0)|([1-9][0-9]*)|0[xX]([0-9A-Fa-f]+)|0([0-7]+)|([1-9][0-9]?)[rR]([0-9A-Za-z]+)|0[0-9]+)(N)?")
   :double (re-pattern "([-+]?[0-9]+(\\.[0-9]*)?([eE][-+]?[0-9]+)?)(M)?")
   :boolean (re-pattern "(?i)true|false")
   :bat #"\^\+\^"
   :caret #"\^"
   :comma #","
   :dash #"-"
   :slash #"/"})

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

(defn split-on-re
  "Useful for its check on blankness and interstitial whitespace trimming."
  [re s]
  (if (non-blank-string s)
    (mapv str/trim (str/split s re))
    []))

(def split-on-bat (partial split-on-re (:bat regexes)))
(def split-on-caret (partial split-on-re (:caret regexes)))
(def split-on-comma (partial split-on-re (:comma regexes)))
(def split-on-dash (partial split-on-re (:dash regexes)))
(def split-on-slash (partial split-on-re (:slash regexes)))

(defn split-on-bats-and-carets
  [s]
  (mapv split-on-caret (split-on-bat s)))

(defn carets->map
  [ks s]
  (->> s
       split-on-caret
       (map non-blank-string)
       (zipmap ks)
       util-map/remove-nils))

(defn bats-and-carets->maps
  [ks s]
  (mapv #(carets->map ks %) (split-on-bat s)))

(defn parse-bounding-box
  [s]
  (when (string? s)
    (let [[lng1 lat1 lng2 lat2 :as bounding-box] (mapv parse-double (split-on-comma s))]
      (when (and (util-number/longitude? lng1)
                 (util-number/latitude? lat1)
                 (util-number/longitude? lng2)
                 (util-number/latitude? lat2))
        bounding-box))))
