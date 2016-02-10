(ns desdemona.functions.sample-functions-test
  (:require [clojure.test :refer [deftest is]]
            [desdemona.functions.sample-functions :refer [transform-segment-shape prepare-rows]]))

(deftest transform-segment-shape-test
  (let [got (transform-segment-shape {"line" [:line]} {:line "this is a log line"})
        expected {"line" "this is a log line"}]
    (is (= got expected))))

(deftest prepare-rows-test
  (let [got (prepare-rows {"line" "this is a log line"})
        expected {:rows [{"line" "this is a log line"}]}]
    (is (= got expected))))
