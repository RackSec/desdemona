(ns desdemona.query-test
  (:require
   [desdemona.query :as q]
   [clojure.test :refer [deftest is are testing]]))

(defn ^:private fn-call
  "Expected parse tree for a function call."
  [f arg]
  [:fn-call [:identifier f] [:identifier arg]])

(deftest infix-parser-tests
  (is (= [:expr [:ipv4-addr "10" "0" "0" "1"]]
         (#'q/infix-parser "10.0.0.1"))
      "ipv4 addresses")
  (is (= [:expr (fn-call "ip" "x")]
         (#'q/infix-parser "ip(x)"))
      "simple fn calls")
  (is (= [:expr [:eq
                 [:identifier "a"]
                 [:identifier "b"]]]
         (#'q/infix-parser "a = b"))
      "equality between identifiers")
  (is (= [:expr [:eq
                 (fn-call "ip" "x")
                 [:ipv4-addr "10" "0" "0" "1"]]]
         (#'q/infix-parser "ip(x) = 10.0.0.1"))
      "equality between fn call and IP address literal")
  (is (= [:expr [:eq
                 (fn-call "ip" "x")
                 (fn-call "ip" "y")]]
         (#'q/infix-parser "ip(x) = ip(y)"))
      "equality between two fn calls")
  (is (= [:expr [:eq
                 (fn-call "ip" "p")
                 (fn-call "ip" "q")
                 (fn-call "ip" "r")
                 (fn-call "ip" "s")]]
         (#'q/infix-parser "ip(p) = ip(q) = ip(r) = ip(s)"))
      "equality between >2 fn calls")
  (testing "equality between two fn calls & literal"
    (is (= [:expr [:eq
                   (fn-call "ip" "x")
                   (fn-call "ip" "y")
                   [:ipv4-addr "10" "0" "0" "1"]]]
           (#'q/infix-parser "ip(x) = ip(y) = 10.0.0.1")))
    (is (= [:expr [:eq
                   (fn-call "ip" "x")
                   [:ipv4-addr "10" "0" "0" "1"]
                   (fn-call "ip" "y")]]
           (#'q/infix-parser "ip(x) = 10.0.0.1 = ip(y)")))
    (is (= [:expr [:eq
                   [:ipv4-addr "10" "0" "0" "1"]
                   (fn-call "ip" "x")
                   (fn-call "ip" "y")]]
           (#'q/infix-parser "10.0.0.1 = ip(x) = ip(y)")))))

(deftest infix-term->dsl-tests
  (are [term dsl] (= dsl (#'q/infix-term->dsl term))
    [:fn-call
     [:identifier "ip"]
     [:identifier "xyzzy"]]
    '(:ip xyzzy)

    [:ipv4-addr "10" "0" "0" "1"]
    "10.0.0.1"))

(deftest infix->dsl-tests
  (is (= '(= (:ip x) "10.0.0.1")
         (q/infix->dsl "ip(x) = 10.0.0.1")))
  (testing "equality between two fn calls & literal"
    (is (= '(= (:ip x) (:ip y) "10.0.0.1")
           (q/infix->dsl "ip(x) = ip(y) = 10.0.0.1")))
    (is (= '(= (:ip x) "10.0.0.1" (:ip y))
           (q/infix->dsl "ip(x) = 10.0.0.1 = ip(y)")))
    (is (= '(= "10.0.0.1" (:ip x) (:ip y))
           (q/infix->dsl "10.0.0.1 = ip(x) = ip(y)")))))

(deftest free-sym-tests
  (is (not (#'q/free-sym? 's))
      "sym not marked as free")
  (is (not (#'q/free-sym? 1))
      "not a symbol")
  (is (#'q/free-sym? (#'q/free-sym 's))
      "sym explicitly marked as free"))

(deftest find-free-vars-tests
  (are [expected query] (= expected (#'q/find-free-vars query))
    #{}
    '()

    #{'x}
    (#'q/dsl->logic '(= (:ip x) "10.0.0.1"))

    #{'y}
    (#'q/dsl->logic '(= (:ip y) "10.0.0.1"))

    #{'x}
    (#'q/dsl->logic '(= "10.0.0.1" (:ip x)))

    #{'x}
    (#'q/dsl->logic '(= (:type x) "egress"))

    #{'x}
    (#'q/dsl->logic '(and (= (:ip x) "10.0.0.1")
                          (= (:type x) "egress")))

    #{'x 'y}
    (#'q/dsl->logic '(= (:ip x) (:ip y)))))

(defmacro with-fake-gensym
  [& body]
  `(let [gensym-count# (atom 0)
         fake-gensym# (fn [& args#]
                        (->> (swap! gensym-count# inc)
                             (str "fake-gensym-")
                             (symbol)))]
     (with-redefs [clojure.core/gensym fake-gensym#]
       ~@body)))

(deftest dsl->logic-tests
  (is (thrown? IllegalArgumentException
               (#'q/dsl->logic '(BOGUS BOGUS BOGUS))))
  (is (= '(clojure.core.logic/featurec x {:ip "10.0.0.1"})
         (#'q/dsl->logic '(= (:ip x) "10.0.0.1"))
         (#'q/dsl->logic '(= "10.0.0.1" (:ip x)))))
  (testing "logic variable is not hard coded to 'x"
    (is (= '(clojure.core.logic/featurec y {:ip "10.0.0.1"})
           (#'q/dsl->logic '(= (:ip y) "10.0.0.1"))
           (#'q/dsl->logic '(= "10.0.0.1" (:ip y))))))
  (testing "logical conjunction"
    (is (= '(clojure.core.logic/conde
             [(clojure.core.logic/featurec x {:ip "10.0.0.1"})
              (clojure.core.logic/featurec x {:type "egress"})])
           (#'q/dsl->logic '(and (= (:ip x) "10.0.0.1")
                                 (= (:type x) "egress"))))))
  (testing "logical disjunction"
    (is (= '(clojure.core.logic/conde
             [(clojure.core.logic/featurec x {:ip "10.0.0.1"})]
             [(clojure.core.logic/featurec x {:type "egress"})])
           (#'q/dsl->logic '(or (= (:ip x) "10.0.0.1")
                                (= (:type x) "egress")))))
    (is (= '(clojure.core.logic/conde
             [(clojure.core.logic/featurec x {:type "egress"})]
             [(clojure.core.logic/featurec x {:ip "10.0.0.1"})])
           (#'q/dsl->logic '(or (= (:type x) "egress")
                                (= (:ip x) "10.0.0.1"))))))
  (testing "multiple literals"
    (is (thrown? IllegalArgumentException
                 (#'q/dsl->logic '(= (:ip x) "10.0.0.1" "10.0.0.1")))
        "repeated but consistent literal")
    (is (thrown? IllegalArgumentException
                 (#'q/dsl->logic '(= (:ip x) "1.1.1.1" "8.8.8.8")))
        "repeated inconsistent literal"))
  (testing "multiple terms unified with a literal"
    (is (= '(clojure.core.logic/all
             (clojure.core.logic/featurec x {:ip "10.0.0.1"})
             (clojure.core.logic/featurec y {:ip "10.0.0.1"}))
           (#'q/dsl->logic '(= (:ip x) (:ip y) "10.0.0.1"))
           (#'q/dsl->logic '(= (:ip x) "10.0.0.1" (:ip y)))
           (#'q/dsl->logic '(= "10.0.0.1" (:ip x) (:ip y))))))
  (testing "linking events"
    (with-fake-gensym
      (is (= '(clojure.core.logic/fresh [fake-gensym-1]
                (clojure.core.logic/featurec x {:ip fake-gensym-1})
                (clojure.core.logic/featurec y {:ip fake-gensym-1}))
             (#'q/dsl->logic '(= (:ip x) (:ip y))))))))

(def events
  [{:ip "10.0.0.1"}
   {:ip "10.0.0.2"
    :type "egress"}
   {:ip "10.0.0.2"
    :type "ingress"}])

(deftest run-dsl-query-tests
  (are [query results] (= results (q/run-dsl-query query events))
    '(= (:ip x) "10.0.0.1")
    [[{:ip "10.0.0.1"}]]

    '(= (:ip y) "10.0.0.1")
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
         :type "ingress"}]]))
  (testing "multi-arity featurec with literal"
    (are [query results] (= results (q/run-dsl-query 10 query events))
      '(= (:ip x) (:ip y) "1.2.3.4")
      []

      '(= (:ip x) (:ip y) "10.0.0.1")
      [[{:ip "10.0.0.1"}
        {:ip "10.0.0.1"}]]))
  (testing "multi-arity features without literal"
    (are [query results] (= results (q/run-dsl-query 10 query events))
      '(= (:ip x) (:ip y))
      [[{:ip "10.0.0.1"}
        {:ip "10.0.0.1"}]
       [{:ip "10.0.0.2" :type "egress"}
        {:ip "10.0.0.2" :type "egress"}]
       [{:ip "10.0.0.2" :type "egress"}
        {:ip "10.0.0.2" :type "ingress"}]
       [{:ip "10.0.0.2" :type "ingress"}
        {:ip "10.0.0.2" :type "egress"}]
       [{:ip "10.0.0.2" :type "ingress"}
        {:ip "10.0.0.2" :type "ingress"}]])))

(deftest run-logic-query-tests
  (are [query results] (= results (#'q/run-logic-query query events))
    'l/fail
    []

    (list clojure.core.logic/featurec
          (#'q/free-sym 'x)
          {:ip "10.0.0.1"})
    [[{:ip "10.0.0.1"}]]

    (list clojure.core.logic/featurec
          (#'q/free-sym 'y)
          {:ip "10.0.0.1"})
    [[{:ip "10.0.0.1"}]])

  (testing "explicit maximum number of results"
    (let [results [[{:ip "10.0.0.1"}]]
          query (list clojure.core.logic/featurec
                      (#'q/free-sym 'x)
                      {:ip "10.0.0.1"})]
      (are [n-results]
           (= results (#'q/run-logic-query n-results query events))
        1
        10))))
