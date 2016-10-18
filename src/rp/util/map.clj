(ns rp.util.map)

(defn select-by
  "Returns a map where the entries satisfy pred."
  [pred m]
  (into {} (filter pred) m))

(defn select-keys-by
  "Returns a map where the keys satisfy pred."
  [pred m]
  (select-by (comp pred key) m))

(defn select-vals-by
  "Returns a map where the vals satisfy pred."
  [pred m]
  (select-by (comp pred val) m))

(defn relocate-keys-by
  "Returns a map where the keys satisfying pred are associated to relocation-key."
  [pred relocation-key m]
  (let [preserved (select-keys-by (complement pred) m)
        relocated (select-keys-by pred m)]
    (assoc preserved relocation-key relocated)))

(defn relocate-vals-by
  "Returns a map where the keys of the vals satisfying pred are associated to relocation-key."
  [pred relocation-key m]
  (let [preserved (select-vals-by (complement pred) m)
        relocated (select-vals-by pred m)]
    (assoc preserved relocation-key relocated)))
