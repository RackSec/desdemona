(ns desdemona.query-test
  (:require
   [desdemona.query :as q]
   [clojure.test :refer [deftest is are]]))

(def dsl->logic
  @#'desdemona.query/dsl->logic)

(deftest dsl->logic-tests
  (is (= '(clojure.core.logic/featurec x {:ip "10.0.0.1"})
         (dsl->logic '(= (:ip x) "10.0.0.1")))))

(deftest dsl-query-tests
  (is (= [[{:ip "10.0.0.1"}]]
         (q/run-dsl-query '(= (:ip x) "10.0.0.1")
                          [{:ip "10.0.0.1"}]))))

(deftest logic-query-tests
  (are [events query results] (= results (q/run-logic-query query events))
    [{:ip "10.0.0.1"}]
    'l/fail
    []

    [{:ip "10.0.0.1"}]
    '(l/featurec x {:ip "10.0.0.1"})
    [[{:ip "10.0.0.1"}]]))
