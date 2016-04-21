(ns desdemona.functions.sample-functions-test
  (:require [clojure.test :refer [deftest is are]]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [desdemona.functions.sample-functions :as f]))

(deftest add-original-wrapper-test
  (let [segment {:some :values}
        got (f/add-original-wrapper segment)]
    (is (= {:original {:some :values}} got))))

(defn raw-example
  [kind]
  (-> (str "test/example_" (name kind) ".json")
      io/resource
      io/reader
      (json/decode-stream ->kebab-case-keyword)))

(defn example
  [kind]
  {:original (raw-example kind)})

(deftest message-origin-test
  (are [origin] (= origin (f/message-origin (raw-example origin)))
    :syslog
    :json
    :falconhose
    :cloudpassage))

(deftest add-message-origin-test
  (are [origin] (= origin ((f/add-message-origin (example origin)) :origin))
    :syslog
    :json
    :falconhose
    :cloudpassage))
