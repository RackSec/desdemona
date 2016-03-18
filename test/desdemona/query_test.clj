(ns desdemona.query-test
  (:require
   [desdemona.query :as q]
   [clojure.test :refer [deftest is are testing]]))

(def dsl->logic
  @#'desdemona.query/dsl->logic)

(deftest dsl->logic-tests
  (is (thrown? IllegalArgumentException (dsl->logic '(BOGUS BOGUS BOGUS))))
  (is (= '(clojure.core.logic/featurec x {:ip "10.0.0.1"})
         (dsl->logic '(= (:ip x) "10.0.0.1"))
         (dsl->logic '(= "10.0.0.1" (:ip x)))))
  (testing "logical conjunction"
    (is (= '(clojure.core.logic/conde
             [(clojure.core.logic/featurec x {:ip "10.0.0.1"})
              (clojure.core.logic/featurec x {:type "egress"})])
           (dsl->logic '(and (= (:ip x) "10.0.0.1")
                             (= (:type x) "egress")))))))

(def events
  [{:ip "10.0.0.1"}])

(deftest dsl-query-tests
  (are [query results] (= results (q/run-dsl-query query events))
    '(= (:ip x) "10.0.0.1")
    [[{:ip "10.0.0.1"}]]

    '(= (:ip x) "BOGUS")
    []

    '(= "10.0.0.1" (:ip x))
    [[{:ip "10.0.0.1"}]]

    '(= "BOGUS" (:ip x))
    [])
  (testing "explicit maximum number of results"
    (let [results [[{:ip "10.0.0.1"}]]
          query '(= (:ip x) "10.0.0.1")]
      (are [n-results] (= results (q/run-dsl-query n-results query events))
        1
        10))))

(deftest logic-query-tests
  (are [query results] (= results (q/run-logic-query query events))
    'l/fail
    []

    '(l/featurec x {:ip "10.0.0.1"})
    [[{:ip "10.0.0.1"}]])
  (testing "explicit maximum number of results"
    (let [results [[{:ip "10.0.0.1"}]]
          query '(l/featurec x {:ip "10.0.0.1"})]
      (are [n-results] (= results (q/run-logic-query n-results query events))
        1
        10))))
