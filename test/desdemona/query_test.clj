(ns desdemona.query-test
  (:require
   [desdemona.query :as q]
   [clojure.test :refer [deftest is are testing]]))

(def dsl->logic
  @#'desdemona.query/dsl->logic)

(deftest dsl->logic-tests
  (is (= '(clojure.core.logic/featurec x {:ip "10.0.0.1"})
         (dsl->logic '(= (:ip x) "10.0.0.1")))))

(def events
  [{:ip "10.0.0.1"}])

(deftest dsl-query-tests
  (are [query results] (= results (q/run-dsl-query query events))
    '(= (:ip x) "10.0.0.1")
    [[{:ip "10.0.0.1"}]]

    '(= (:ip x) "BOGUS")
    [])
  (testing "explicit maximum number of results"
    (are [n-results]
        (= [[{:ip "10.0.0.1"}]]
           (q/run-dsl-query n-results '(= (:ip x) "10.0.0.1") events))
      1
      10)))

(deftest logic-query-tests
  (are [query results] (= results (q/run-logic-query query events))
    'l/fail
    []

    '(l/featurec x {:ip "10.0.0.1"})
    [[{:ip "10.0.0.1"}]])
  (testing "explicit maximum number of results"
    (are [n-results]
        (= [[{:ip "10.0.0.1"}]]
           (q/run-logic-query n-results
                              '(l/featurec x {:ip "10.0.0.1"})
                              events))
      1
      10)))
