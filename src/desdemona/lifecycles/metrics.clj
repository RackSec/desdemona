(ns desdemona.lifecycles.metrics)

(defn add-metrics
  "Add's throughput and latency metrics to a task"
  [job task opts]
  (update-in
   job
   [:lifecycles]
   conj
   (merge
    {:lifecycle/task task,
     :lifecycle/calls :onyx.lifecycle.metrics.metrics/calls}
    opts)))
