(ns desdemona.query-test
  (:require
   [desdemona.query :as q]
   [clojure.test :refer [deftest is]]))

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
  (is (= []
         (q/run-logic-query 'l/fail
                            [{:ip "10.0.0.1"}])))
  (is (= [[{:ip "10.0.0.1"}]]
         (q/run-logic-query '(l/featurec x {:ip "10.0.0.1"})
                            [{:ip "10.0.0.1"}]))))
