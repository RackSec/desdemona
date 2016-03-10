(ns desdemona.lifecycles.metrics)

(defn add-metrics
  "Add's throughput and latency metrics to a task"
  [job task opts]
  (let [metrics {:lifecycle/task task
                 :lifecycle/calls :onyx.lifecycle.metrics.metrics/calls}]
    (update job :lifecycles conj (merge metrics opts))))
