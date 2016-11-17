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
  "Returns a map where the keys satisfying pred are associated to the path of the
   relocation-ks key sequence."
  [pred relocation-ks m]
  (let [preserved (select-keys-by (complement pred) m)
        relocated (select-keys-by pred m)]
    (assoc-in preserved relocation-ks relocated)))

(defn relocate-vals-by
  "Returns a map where the keys of the vals satisfying pred are associated to
   the path of the relocation-ks key sequence."
  [pred relocation-ks m]
  (let [preserved (select-vals-by (complement pred) m)
        relocated (select-vals-by pred m)]
    (assoc-in preserved relocation-ks relocated)))

(defn dissoc-in
  "Dissociate a value in a nested assocative structure, identified by a sequence
  of keys. Any collections left empty by the operation will be dissociated from
  their containing structures."
  [m ks]
  (if-let [[k & ks] (seq ks)]
    (if (seq ks)
      (let [v (dissoc-in (get m k) ks)]
        (if (empty? v)
          (dissoc m k)
          (assoc m k v)))
      (dissoc m k))
    m))

(defn relocate-keys
  "Returns a map where the value in a nested associative structure is relocated
   from the path specified by key sequence ks1 to the path of key sequence ks2."
  [m ks1 ks2]
  (let [val-to-move (get-in m ks1)]
    (cond-> (dissoc-in m ks1)
      val-to-move (assoc-in ks2 val-to-move))))
