(ns desdemona.lifecycles.logging-test
  (:require
   [desdemona.lifecycles.logging :as l]
   [desdemona.test-macros :refer [with-out-str-and-result]]
   [clojure.test :refer [deftest is]]
   [clojure.string :as s]
   [clojure.core.match :refer [match]]))

(def sample-event
  {:onyx.core/task-map {:onyx/name "xyzzy"}
   :onyx.core/results {:tree [{:leaves [{:message 1}
                                        {:message 2}
                                        {:message 3}]}
                              {:leaves [{:message 4}
                                        {:message 5}
                                        {:message 6}]}
                              {:leaves [{:message 7}
                                        {:message 8}
                                        {:message 9}]}]}})

(def sample-lifecycle
  nil)

(deftest log-batch-tests
  (let [[result stdout] (with-out-str-and-result
                          (l/log-batch sample-event sample-lifecycle))
        lines (map #(s/split % #" " 7) (s/split-lines stdout))
        parsed-lines (map (fn [line]
                            (match [line]
                              [[date time hostname level source sep message]]
                              {:date date
                               :time time
                               :hostname hostname
                               :level level
                               :source source
                               :sep sep
                               :message message}))
                          lines)
        match-message (fn [message]
                        (-> #"xyzzy logging segment: (?<n>\d)"
                            (re-matches message)
                            (second)))]
    (is (= {} result))
    (is (= (count lines) 9))
    (is (apply = (map :hostname parsed-lines)))
    (is (apply = "INFO"
               (map :level parsed-lines)))
    (is (apply = "[desdemona.lifecycles.logging]"
               (map :source parsed-lines)))
    (is (apply = "-" (map :sep parsed-lines)))
    (is (= (map str (range 1 10))
           (map (comp match-message :message) parsed-lines)))))
