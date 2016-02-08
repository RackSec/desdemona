(ns desdemona.tasks.kafka-test
  (:require [clojure.test :refer [deftest is]]
            [desdemona.tasks.kafka :refer [deserialize-message-raw]]))

(deftest deserialize-message-raw-test
  (let [got (deserialize-message-raw (.getBytes "this is raw text"))
        expected {:line "this is raw text"}]
    (is (= got expected))))
