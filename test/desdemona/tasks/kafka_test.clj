(ns desdemona.tasks.kafka-test
  (:require [clojure.test :refer [deftest is]]
            [desdemona.tasks.kafka :refer [deserialize-message-raw]]))

(deftest deserialize-message-raw-test
  (let [got (deserialize-message-raw (.getBytes "this is raw text"))
        expected {:line "this is raw text"}]
    (is (= got expected))))

(deftest deserialize-message-raw-fails-test
  (let [got (deserialize-message-raw "this should be bytes")]
    (is (= (.getMessage (got :error)) "No matching ctor found for class java.lang.String"))))
