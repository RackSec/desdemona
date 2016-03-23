(ns desdemona.lifecycles.logging
  (:require [taoensso.timbre :refer [info]]))

  (let [task-name (:onyx/name (:onyx.core/task-map event))]
    (doseq [m (map :message (mapcat :leaves (:tree (:onyx.core/results event))))]
(defn log-batch
  [event lifecycle]
      (info task-name "logging segment:" m)))
  {})

(def log-calls
  {:lifecycle/after-batch log-batch})

(defn add-logging
  "Adds logging output to a task's output-batch."
  [job task]
  (if-let [entry (first (filter #(= (:onyx/name %) task) (:catalog job)))]
    (update-in
     job
     [:lifecycles]
     conj
     {:lifecycle/task task
      :lifecycle/calls ::log-calls})))
