(ns desdemona.tasks.sql
  (:require [schema.core :as s]
            [taoensso.timbre :refer [info]]))

;; TODO, add read-rows function task

(defn add-sql-insert-output
  "Adds an sql insert rows output task to a job"
  [job task opts]
  (-> job
      (update :catalog conj (merge {:onyx/name task
                                    :onyx/plugin :onyx.plugin.sql/write-rows
                                    :onyx/type :output
                                    :onyx/medium :sql
                                    :onyx/doc "Writes segments from the :rows keys to the SQL database"}
                                   opts))
      (update :lifecycles conj {:lifecycle/task task
                                :lifecycle/calls :onyx.plugin.sql/write-rows-calls})))
