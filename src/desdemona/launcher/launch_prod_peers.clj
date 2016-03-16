(ns desdemona.launcher.launch-prod-peers
  (:require [clojure.core.async :refer [<!! chan]]
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

(defn stdout-logger
  "Logger to output on std-out, for use with docker-compose"
  [data]
  (let [{:keys [output-fn]} data]
    (println (output-fn data))))

(defn ^:private read-config!
  []
  (aero/read-config (io/resource "config.edn") {:profile :default}))

(defn -main [n & args]
  (let [n-peers (Integer/parseInt n)
        config (read-config!)
        peer-config (assoc
                     (:peer-config config)
                     :onyx.log/config
                     {:appenders
                      {:stdout
                       {:enabled? true,
                        :async? false,
                        :output-fn t/default-output-fn,
                        :fn stdout-logger}}})
        peer-group (onyx.api/start-peer-group peer-config)
        env (onyx.api/start-env (:env-config config))
        peers (onyx.api/start-peers n-peers peer-group)]
    (println "Attempting to connect to Zookeeper: " (:zookeeper/address peer-config))
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread.
                       (fn []
                         (doseq [v-peer peers]
                           (onyx.api/shutdown-peer v-peer))
                         (onyx.api/shutdown-peer-group peer-group)
                         (shutdown-agents))))
    (println "Started peers. Blocking forever.")
    ;; Block forever.
    (<!! (chan))))
