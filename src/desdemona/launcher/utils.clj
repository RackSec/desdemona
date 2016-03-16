(ns desdemona.launcher.utils
  (:require
   [clojure.core.async :as a]))

(defn block-forever!
  []
  (a/<!! (a/chan)))
