(ns desdemona.jobs.sample-job-test
  (:require [clojure.test :refer [deftest is]]
            [desdemona.jobs.sample-submit-job :refer [build-job]]
            [desdemona.utils :refer [find-task]]
            ; Make the plugins load
            [onyx.plugin.kafka]
            [onyx.plugin.seq]
            [onyx.plugin.sql]))

(deftest build-job-test
  (let [{:keys [catalog workflow lifecycles]} (build-job)
        expected-catalog-names [:original-wrapper
                                :determine-origin
                                :read-lines
                                :write-swift]
        expected-workflow [[:read-lines :original-wrapper]
                           [:original-wrapper :determine-origin]
                           [:determine-origin :write-swift]]
        expected-lifecycles
        [{:lifecycle/task :write-swift
          :lifecycle/calls :desdemona.lifecycles.logging/log-calls}
         {:lifecycle/task :read-lines
          :lifecycle/calls :desdemona.lifecycles.logging/log-calls}
         {:lifecycle/task :read-lines
          :lifecycle/calls :onyx.plugin.kafka/read-messages-calls}]]
    (is (= expected-catalog-names
           (map :onyx/name catalog)))
    (is (= "test1"
           ((find-task catalog :read-lines) :kafka/topic)))
    (is (thrown? Exception ((find-task catalog :doesnt-exist) :kafka/topic)))
    (is (= expected-workflow workflow))
    (is (= expected-lifecycles lifecycles))))
