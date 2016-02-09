(ns desdemona.jobs.sample-submit-job
  (:require [desdemona.catalogs.sample-catalog :refer [build-catalog]]
            [desdemona.tasks.kafka :refer [add-kafka-input add-kafka-output]]
            [desdemona.tasks.core-async :refer [add-core-async-input add-core-async-output]]
            [desdemona.tasks.sql :refer [add-sql-partition-input add-sql-insert-output]]
            [desdemona.tasks.file-input :refer [add-seq-file-input]]
            [desdemona.lifecycles.sample-lifecycle :refer [build-lifecycles]]
            [desdemona.lifecycles.metrics :refer [add-metrics]]
            [desdemona.lifecycles.logging :refer [add-logging]]
            [desdemona.workflows.sample-workflow :refer [build-workflow]]
            [aero.core :refer [read-config]]
            [onyx.api]))

;;;;
;; Lets build a job
;; Since we always run in Docker Compose, kafka is added as an input, and onyx-sql is used as the output

(defn build-job []
  (let [batch-size 1
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
                                      :kafka/deserializer-fn :desdemona.tasks.kafka/deserialize-message-raw
                                      :kafka/offset-reset :smallest})
        (add-sql-insert-output :write-lines {:onyx/batch-size batch-size
                                             :sql/classname "com.mysql.jdbc.Driver"
                                             :sql/subprotocol "mysql"
                                             :sql/subname "//db:3306/logs"
                                             :sql/user "onyx"
                                             :sql/password "onyx"
                                             :sql/table :logLines})
        (add-logging :read-lines)
        (add-logging :write-lines))))

(defn -main [& args]
  (let [config (read-config (clojure.java.io/resource "config.edn") {:profile :dev})
        peer-config (get config :peer-config)
        job (build-job)]
    (let [{:keys [job-id]} (onyx.api/submit-job peer-config job)]
      (println "Submitted job: " job-id))))
