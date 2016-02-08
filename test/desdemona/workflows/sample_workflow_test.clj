(ns desdemona.workflows.sample-workflow-test
  (:require [clojure.test :refer [deftest is]]
            [desdemona.workflows.sample-workflow :refer [build-workflow]]))

(deftest dev-workflow-test
  (let [expected [[:read-lines :extract-line-info]
                  [:extract-line-info :prepare-rows]
                  [:prepare-rows :write-lines]]
        got (build-workflow {:mode :dev})]
    (is (= got expected))))

(deftest prod-workflow-test
  (let [expected [[:read-lines :extract-line-info]
                  [:extract-line-info :prepare-rows]
                  [:prepare-rows :write-lines]]
        got (build-workflow {:mode :prod})]
    (is (= got expected))))
