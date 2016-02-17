(ns desdemona.utils
  (:require [clojure.core.async :refer [chan sliding-buffer >!!]]
            [clojure.java.io :refer [resource]]))

;;;; Test utils ;;;;

(defn find-task
  "Finds the catalog entry where the :onyx/name key equals task-name"
  [catalog task-name]
  (let [matches (filter #(= task-name (:onyx/name %)) catalog)]
    (when-not (seq matches)
      (throw (ex-info (format "Couldn't find task %s in catalog" task-name)
                      {:catalog catalog :task-name task-name})))
    (first matches)))
