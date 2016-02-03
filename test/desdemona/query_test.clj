(ns desdemona.query-test
  (:require
   [desdemona.query :as q]
   [clojure.test :refer [deftest is]]))

(deftest query-tests
  (is (= [{:ip "10.0.0.1"}]
         (q/query '(== (:ip x) "10.0.0.1")
                  [{:ip "10.0.0.1"}]))))
