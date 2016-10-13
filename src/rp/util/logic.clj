(ns rp.util.logic)

(defn xor
  [p q]
  (and
   (or p q)
   (not (and p q))))
