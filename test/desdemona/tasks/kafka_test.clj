(ns desdemona.tasks.kafka-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [desdemona.tasks.kafka :refer [deserialize-message-raw deserialize-message-json]]))

(deftest deserialize-message-json-test
  (let [in {"this" "will"
            "BE" "json"
            "WITH_DIFFERENT" "forms"
            "_that_we" "will change"}
        encoded (json/write-str in)
        got (deserialize-message-json (.getBytes encoded))]
    (is (= {:this "will"
            :be "json"
            :with-different "forms"
            :that-we "will change"} got))))

(deftest deserialize-message-json-falconhose-test
  (let [raw (slurp (io/file (io/resource "test/example_falconhose.json")))
        decoded (json/read-str raw :key-fn ->kebab-case-keyword)
        got (deserialize-message-json (.getBytes raw))]
    (is (= (decoded :facility) (got :facility)))
    (is (= (-> decoded :parsed :metadata :customer-id-string) (-> got :parsed :metadata :customer-id-string)))))

(deftest deserialize-message-json-fails-test
  (let [got (deserialize-message-json "this should be bytes")]
    (is (.startsWith (.getMessage (got :error)) "JSON error"))))

(deftest deserialize-message-invalid-json-fails-test
  (let [got (deserialize-message-json (.getBytes "not json"))]
    (is (.startsWith (.getMessage (got :error)) "JSON error"))))

(deftest deserialize-message-raw-test
  (let [got (deserialize-message-raw (.getBytes "this is raw text"))
        expected {:line "this is raw text"}]
    (is (= expected got))))

(deftest deserialize-message-raw-fails-test
  (let [got (deserialize-message-raw "this should be bytes")]
    (is (= "No matching ctor found for class java.lang.String" (.getMessage (got :error))))))
