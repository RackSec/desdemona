(ns desdemona.metrics-test
  (:require
   [desdemona.lifecycles.metrics :as m]
   [clojure.test :as t]))

(deftest add-metrics-tests
  (let [job {:lifecycles []}
        task {}
        lifecycle {:lifecycle/task task
                   :lifecycle/calls :onyx-lifecycle.metrics.metrics/calls}
        opts {:pedantic true}
        lifecycle-with-opts (merge lifecycle opts)]
    (is (= {:lifecycles [lifecycle]}
           (m/add-metrics job task {})))
    (is (= {:lifecycles [lifecycle-with-opts]}
           (m/add-metrics job task opts)))))
