(ns desdemona.tasks.kafka-test
  (:require [clojure.test :refer [deftest is]]
            [cheshire.core :as json]
            [desdemona.tasks.kafka :refer [deserialize-message-raw deserialize-message-json]]))

(deftest deserialize-message-json-test
  (let [in {"this" "will" "be" "json"}
        encoded (json/generate-string in)
        got (deserialize-message-json (.getBytes encoded))]
    (is (= got {:this "will" :be "json"}))))

(deftest deserialize-message-json-fails-test
  (let [got (deserialize-message-json "this should be bytes")]
    (is (.startsWith (.getMessage (got :error)) "Unrecognized token"))))

(deftest deserialize-message-invalid-json-fails-test
  (let [got (deserialize-message-json (.getBytes "not json"))]
    (is (.startsWith (.getMessage (got :error)) "Unrecognized token"))))

(deftest deserialize-message-raw-test
  (let [got (deserialize-message-raw (.getBytes "this is raw text"))
        expected {:line "this is raw text"}]
    (is (= got expected))))

(deftest deserialize-message-raw-fails-test
  (let [got (deserialize-message-raw "this should be bytes")]
    (is (= (.getMessage (got :error)) "No matching ctor found for class java.lang.String"))))
