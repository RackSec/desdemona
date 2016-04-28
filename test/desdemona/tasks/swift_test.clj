(ns desdemona.tasks.swift-test
  (:require [clojure.test :refer [deftest is]]
            [desdemona.tasks.swift :refer [add-swift-output]]))

(deftest add-swift-output-test
  (let [job {:catalog []}
        no-opts (add-swift-output job :task-name {})
        with-opts (add-swift-output job :other-task-name {:one 1})]
    (is (= {:catalog [{:onyx/name :task-name
                       :onyx/plugin :desdemona.plugins.swift/write-batch
                       :onyx/type :output
                       :onyx/medium :swift
                       :onyx/doc "Write segments to Swift"}]} no-opts))
    (is (= {:catalog [{:onyx/name :other-task-name
                       :onyx/plugin :desdemona.plugins.swift/write-batch
                       :onyx/type :output
                       :onyx/medium :swift
                       :onyx/doc "Write segments to Swift"
                       :one 1}]} with-opts))))
