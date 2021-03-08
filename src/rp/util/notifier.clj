(ns rp.util.notifier)

(defprotocol Notifier
  ;; event is a keyword naming the type of event; data is a map of details about the event
  (notify! [this event data]))

(defn maybe-notify!
  "Convenience wrapper to avoid NPE on nil `notifier`"
  [notifier event data]
  (when notifier (notify! notifier event data)))
