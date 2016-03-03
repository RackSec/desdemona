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
    (is (= got {:original {:some :values}}))))

(deftest prepare-rows-test
  (let [got (prepare-rows {"line" "this is a log line"})
        expected {:rows [{"line" "this is a log line"}]}]
    (is (= got expected))))

(defn raw-example
  [kind]
  (kwify-map (json/parse-stream (bs/to-reader (io/file (io/resource (str "test/example_" (name kind) ".json")))) true)))

(defn example
  [kind]
  {:original (raw-example kind)})

(deftest message-origin-test
  (are [expected got] (= expected got)
    :syslog (message-origin (raw-example :syslog))
    :json (message-origin (raw-example :json))
    :falconhose (message-origin (raw-example :falconhose))
    :cloudpassage (message-origin (raw-example :cloudpassage))))

(deftest add-message-origin-test
  (are [expected got] (= expected (got :origin))
    :syslog (add-message-origin (example :syslog))
    :json (add-message-origin (example :json))
    :falconhose (add-message-origin (example :falconhose))
    :cloudpassage (add-message-origin (example :cloudpassage))))

(deftest build-row-test
  (let [input {:origin :somewhere :original {:message "This is the message!"}}
        expected {:line "somewhere: This is the message!"}
        got (build-row input)]
    (is (= got expected))))
