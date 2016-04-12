(ns desdemona.tasks.swift)

(defn add-swift-output
  "Adds a Swift output task to a job"
  [job task opts]
  (-> job
      (update :catalog conj (merge {:onyx/name task
                                    :onyx/plugin :desdemona.plugins.swift/write-batch
                                    :onyx/type :output
                                    :onyx/medium :swift
                                    :onyx/doc "Write segments to Swift"}
                                   opts))))
