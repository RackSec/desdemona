(ns desdemona.launcher.utils
  (:require
   [clojure.core.async :refer [chan <!!]]))

(defn block-forever!
  []
  (<!! (chan)))
