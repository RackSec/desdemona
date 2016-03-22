(ns desdemona.launcher.utils
  (:require
   [clojure.core.async :as a]))

(defn block-forever!
  "Blocks forever."
  []
  (a/<!! (a/chan)))
