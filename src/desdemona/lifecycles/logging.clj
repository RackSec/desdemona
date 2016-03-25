(ns desdemona.lifecycles.logging
  (:require [taoensso.timbre :refer [info]]))

(defn log-batch
  [event lifecycle]
  (let [task-name (-> event
                      :onyx.core/task-map
                      :onyx/name)]
    (doseq [m (->> event
                   :onyx.core/results
                   :tree
                   (mapcat :leaves)
                   (map :message))]
      (info task-name "logging segment:" m)))
  {})

(def log-calls
  {:lifecycle/after-batch log-batch})

(defn add-logging
  "Adds logging output to a task's output-batch."
  [job task]
  (if-let [entry (first (filter #(= (:onyx/name %) task) (:catalog job)))]
    (update job :lifecycles conj {:lifecycle/task task
                                  :lifecycle/calls ::log-calls})))
