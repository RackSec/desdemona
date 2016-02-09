(ns desdemona.jobs.sample-job-test
  (:require [clojure.test :refer [deftest is]]
            [desdemona.jobs.sample-submit-job :refer [build-job]]
            ; Make the plugins load
            [onyx.plugin.kafka]
            [onyx.plugin.seq]
            [onyx.plugin.sql]))

(defn by-name [catalog name]
  (first (filter (fn [x] (= name (x :onyx/name))) catalog)))

(deftest build-job-test
  (let [job (build-job)
        expected-catalog-names [:extract-line-info :prepare-rows :read-lines :write-lines]
        catalog (job :catalog)
        workflow (job :workflow)
        expected-workflow [[:read-lines :extract-line-info]
                           [:extract-line-info :prepare-rows]
                           [:prepare-rows :write-lines]]
        lifecycles (job :lifecycles)
        expected-lifecycles [{:lifecycle/task :write-lines :lifecycle/calls :desdemona.lifecycles.logging/log-calls}
                             {:lifecycle/task :read-lines :lifecycle/calls :desdemona.lifecycles.logging/log-calls}
                             {:lifecycle/task :write-lines :lifecycle/calls :onyx.plugin.sql/write-rows-calls}
                             {:lifecycle/task :read-lines, :lifecycle/calls :onyx.plugin.kafka/read-messages-calls}]]
    (is (= (map :onyx/name catalog) expected-catalog-names))
    (is (= ((by-name catalog :read-lines) :kafka/topic) "test1"))
    (is (= ((by-name catalog :write-lines) :sql/table) :logLines))
    (is (= workflow expected-workflow))
    (is (= lifecycles expected-lifecycles))))
