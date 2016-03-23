(ns desdemona.launcher.utils
  (:require
   [clojure.core.async :as a]
   [aero.core :as aero]
   [clojure.java.io :as io]))

(defn block-forever!
  "Blocks forever."
  []
  (a/<!! (a/chan)))

(defn read-config!
  []
  (aero/read-config (io/resource "config.edn") {:profile :default}))

(defn add-shutdown-hook!
  [f]
  (.addShutdownHook (Runtime/getRuntime) (Thread. f)))
