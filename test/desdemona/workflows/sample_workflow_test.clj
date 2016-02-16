(ns desdemona.workflows.sample-workflow-test
  (:require [clojure.test :refer [deftest is]]
            [desdemona.workflows.sample-workflow :refer [build-workflow]]))

(deftest build-workflow-test
  (let [expected [[:read-lines :extract-line-info]
                  [:extract-line-info :prepare-rows]
                  [:prepare-rows :write-lines]]
        got (build-workflow)]
    (is (= got expected))))
