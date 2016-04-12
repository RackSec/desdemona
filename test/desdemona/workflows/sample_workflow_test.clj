(ns desdemona.workflows.sample-workflow-test
  (:require [clojure.test :refer [deftest is]]
            [desdemona.workflows.sample-workflow :refer [build-workflow]]))

(deftest build-workflow-test
  (let [expected [[:read-lines :original-wrapper]
                  [:original-wrapper :determine-origin]
                  [:determine-origin :write-swift]]
        got (build-workflow)]
    (is (= expected got))))
