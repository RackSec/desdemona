(ns desdemona.query-test
  (:require
   [desdemona.query :as q]
   [clojure.test :refer [deftest is are testing]]))

(deftest infix-parser-tests
  (is (= [:expr [:ipv4-address "10" "0" "0" "1"]]
         (#'q/infix-parser "10.0.0.1"))
      "ipv4 addresses")
  (is (= [:expr [:fn-call
                 [:identifier "ip"]
                 [:identifier "x"]]]
         (#'q/infix-parser "ip(x)"))
      "simple fn calls")
  (is (= [:expr [:eq
                 [:identifier "a"]
                 [:identifier "b"]]]
         (#'q/infix-parser "a = b"))
      "equality between identifiers")

(deftest infix->dsl-tests
  (is (= '(= (:ip x) "10.0.0.1")
         (q/infix->dsl "ip(x) = 10.0.0.1"))))

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
                             (= (:type x) "egress"))))))
  (testing "logical disjunction"
    (is (= '(clojure.core.logic/conde
             [(clojure.core.logic/featurec x {:ip "10.0.0.1"})]
             [(clojure.core.logic/featurec x {:type "egress"})])
           (dsl->logic '(or (= (:ip x) "10.0.0.1")
                            (= (:type x) "egress")))))
    (is (= '(clojure.core.logic/conde
             [(clojure.core.logic/featurec x {:type "egress"})]
             [(clojure.core.logic/featurec x {:ip "10.0.0.1"})])
           (dsl->logic '(or (= (:type x) "egress")
                            (= (:ip x) "10.0.0.1")))))))

(def events
  [{:ip "10.0.0.1"}
   {:ip "10.0.0.2"
    :type "egress"}
   {:ip "10.0.0.2"
    :type "ingress"}])

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
        10)))
  (testing "conjunction"
    (are [query results] (= results (q/run-dsl-query query events))
      '(and (= (:ip x) "10.0.0.1")
            (= (:type x) "egress"))
      []

      '(and (= (:ip x) "10.0.0.2")
            (= (:type x) "egress"))
      [[{:ip "10.0.0.2"
         :type "egress"}]]

      '(and (= (:type x) "egress")
            (= (:ip x) "10.0.0.2"))
      [[{:ip "10.0.0.2"
         :type "egress"}]]))
  (testing "disjunction"
    (are [query results] (= results (q/run-dsl-query 10 query events))
      '(or (= (:ip x) "1.2.3.4")
           (= (:type x) "bogus"))
      []

      '(or (= (:ip x) "10.0.0.1")
           (= (:type x) "egress"))
      [[{:ip "10.0.0.1"}]
       [{:ip "10.0.0.2"
         :type "egress"}]]

      '(or (= (:ip x) "10.0.0.1")
           (= (:type x) "ingress"))
      [[{:ip "10.0.0.1"}]
       [{:ip "10.0.0.2"
         :type "ingress"}]]

      '(or (= (:ip x) "10.0.0.2")
           (= (:type x) "egress"))
      [[{:ip "10.0.0.2"    ;; ip clause succeeded
         :type "egress"}]
       [{:ip "10.0.0.2"    ;; type clause succeeded
         :type "egress"}]
       [{:ip "10.0.0.2"    ;; ip clause succeeded
         :type "ingress"}]]

      '(or (= (:type x) "egress")
           (= (:ip x) "10.0.0.2"))
      [[{:ip "10.0.0.2"    ;; type clause succeeded
         :type "egress"}]
       [{:ip "10.0.0.2"    ;; ip clause succeeded
         :type "egress"}]
       [{:ip "10.0.0.2"    ;; ip clause succeeded
         :type "ingress"}]])))

(deftest logic-query-tests
  (are [query results] (= results (#'q/run-logic-query query events))
    'l/fail
    []

    '(l/featurec x {:ip "10.0.0.1"})
    [[{:ip "10.0.0.1"}]])
  (testing "explicit maximum number of results"
    (let [results [[{:ip "10.0.0.1"}]]
          query '(l/featurec x {:ip "10.0.0.1"})]
      (are [n-results] (= results (#'q/run-logic-query n-results query events))
        1
        10))))
