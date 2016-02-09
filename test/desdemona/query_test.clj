(ns desdemona.query-test
  (:require
   [desdemona.query :as q]
   [clojure.test :refer [deftest is]]))

(deftest query-tests
  (is (= []
         (q/run-query 'l/fail
                      [{:ip "10.0.0.1"}])))
  (is (= [[{:ip "10.0.0.1"}]]
         (q/run-query '(l/featurec x {:ip "10.0.0.1"})
                      [{:ip "10.0.0.1"}]))))
