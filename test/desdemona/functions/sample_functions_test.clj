(ns desdemona.functions.sample-functions-test
  (:require [clojure.test :refer [deftest is are]]
            [clojure.java.io :as io]
            [byte-streams :as bs]
            [cheshire.core :as json]
            [desdemona.functions.sample-functions :refer [prepare-rows message-origin add-message-origin build-row]]))

(deftest prepare-rows-test
  (let [got (prepare-rows {"line" "this is a log line"})
        expected {:rows [{"line" "this is a log line"}]}]
    (is (= got expected))))

(defn example
  [kind]
  (json/parse-stream (bs/to-reader (io/file (io/resource (str "test/example_" (name kind) ".json")))) true))

(deftest message-origin-test
  (are [expected got] (= expected got)
       :syslog (message-origin (example :syslog))
       :json (message-origin (example :json))
       :falconhose (message-origin (example :falconhose))
       :cloudpassage (message-origin (example :cloudpassage))))

(deftest add-message-origin-test
  (let [got (add-message-origin (example :syslog))]
    (is (= :syslog (got :origin)))))

(deftest build-row-test
  (let [input {:origin :somewhere :MESSAGE "This is the message!"}
        expected {:line "somewhere: This is the message!"}
        got (build-row input)]
    (is (= got expected))))
