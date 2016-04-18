(ns desdemona.jobs.sample-submit-job
  (:require [desdemona.catalogs.sample-catalog :refer [build-catalog]]
            [desdemona.tasks.kafka :refer [add-kafka-input]]
            [desdemona.tasks.swift :refer [add-swift-output]]
            [desdemona.lifecycles.sample-lifecycle :refer [build-lifecycles]]
            [desdemona.lifecycles.logging :refer [add-logging]]
            [desdemona.workflows.sample-workflow :refer [build-workflow]]
            [clojure.java.io :as io]
            [aero.core :refer [read-config]]
            [onyx.api]))

;;;;
;; Lets build a job
;; Since we always run in Docker Compose, kafka is added as an input, and onyx-sql is used as the output

(defn build-job []
  (let [batch-size 5
        batch-timeout 1000
        base-job {:catalog (build-catalog batch-size batch-timeout)
                  :lifecycles (build-lifecycles)
                  :workflow (build-workflow)
                  :task-scheduler :onyx.task-scheduler/balanced}]
    (-> base-job
        (add-kafka-input :read-lines {:onyx/batch-size batch-size
                                      :onyx/max-peers 1
                                      :kafka/topic "test1"
                                      :kafka/group-id "onyx-consumer"
                                      :kafka/zookeeper "zk:2181"
                                      :kafka/deserializer-fn :desdemona.tasks.kafka/deserialize-message-json
                                      :kafka/offset-reset :smallest})
        (add-swift-output :write-swift {:onyx/batch-size batch-size
                                        :onyx/batch-timeout batch-timeout
                                        :swift/auth-url "http://mimic:8900/identity/v2.0/tokens"
                                        :swift/username "mimic"
                                        :swift/api-key "12345"})
        (add-logging :read-lines)
        (add-logging :write-swift))))

(defn -main [& args]
  (let [config (read-config (io/resource "config.edn") {:profile :dev})
        peer-config (get config :peer-config)
        job (build-job)
        {:keys [job-id]} (onyx.api/submit-job peer-config job)]
    (println "Submitted job: " job-id)))
