(ns desdemona.launcher.launch-prod-peers
  (:require [desdemona.launcher.utils :as utils]
            [aero.core :as aero]
            [taoensso.timbre :as t]
            [clojure.java.io :as io]
            [onyx.plugin.kafka]
            [onyx.plugin.sql]
            [onyx.plugin.core-async]
            [onyx.plugin.seq]
            [desdemona.functions.sample-functions]
            [desdemona.jobs.sample-submit-job]
            [desdemona.lifecycles.sample-lifecycle])
  (:gen-class))

(defn ^:private read-config!
  []
  (aero/read-config (io/resource "config.edn") {:profile :default}))

(defn ^:private add-shutdown-hook!
  [f]
  (.addShutdownHook (Runtime/getRuntime) (Thread. f)))

(defn -main [n & args]
  (let [n-peers (Integer/parseInt n)
        {:keys [peer-config env-config]} (read-config!)
        peer-group (onyx.api/start-peer-group peer-config)
        env (onyx.api/start-env env-config)
        peers (onyx.api/start-peers n-peers peer-group)]
    (println "Connecting to Zookeeper: " (:zookeeper/address peer-config))
    (add-shutdown-hook! (fn []
                          (onyx.api/shutdown-peers peers)
                          (onyx.api/shutdown-peer-group peer-group)
                          (shutdown-agents)))
    (println "Started peers. Blocking forever.")
    (utils/block-forever!)))
