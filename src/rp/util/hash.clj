(ns rp.util.hash
  (:import [java.security MessageDigest]
           [java.math BigInteger]))

(defn md5
  [^String s]
  (when s
    (let [algorithm (MessageDigest/getInstance "MD5")
          raw (.digest algorithm (.getBytes s))
          sign 1]
      (format "%032x" (BigInteger. sign raw)))))
