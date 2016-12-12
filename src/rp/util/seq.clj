(ns rp.util.seq)

(defn tuples->maps
  "Return a vector of maps where each map has the specified keys with values from each tuple."
  [ks tuples]
  (mapv #(zipmap ks %) tuples))
