(ns desdemona.functions.sample-functions-test
  (:require [clojure.test :refer [deftest is are]]
            [clojure.java.io :as io]
            [byte-streams :as bs]
            [cheshire.core :as json]
            [desdemona.utils :refer [kwify-map]]
            [desdemona.functions.sample-functions :refer [prepare-rows message-origin add-message-origin build-row add-original-wrapper]]))

(deftest add-original-wrapper-test
  (let [segment {:some :values}
        got (add-original-wrapper segment)]
    (is (= {:original {:some :values}} got))))

(deftest prepare-rows-test
  (let [got (prepare-rows {"line" "this is a log line"})
        expected {:rows [{"line" "this is a log line"}]}]
    (is (= expected got))))

(defn raw-example
  [kind]
  (kwify-map (json/parse-stream (bs/to-reader (io/file (io/resource (str "test/example_" (name kind) ".json")))) true)))

(defn example
  [kind]
  {:original (raw-example kind)})

(deftest message-origin-test
  (are [origin] (= origin (message-origin (raw-example origin)))
    :syslog
    :json
    :falconhose
    :cloudpassage))

(deftest add-message-origin-test
  (are [origin] (= origin ((add-message-origin (example origin)) :origin))
    :syslog
    :json
    :falconhose
    :cloudpassage))

(deftest build-row-test
  (let [input {:origin :somewhere :original {:message "This is the message!"}}
        expected {:line "somewhere: This is the message!"}
        got (build-row input)]
    (is (= expected got))))
