(ns desdemona.utils
  (:require [clojure.core.async :refer [chan sliding-buffer >!!]]
            [clojure.java.io :refer [resource]]))

;;;; Test utils ;;;;

(def zk-address "127.0.0.1")

(def zk-port 2188)

(def zk-str (str zk-address ":" zk-port))

(defn find-task
  "Finds the catalog entry where the :onyx/name key equals task-name"
  [catalog task-name]
  (let [matches (filter #(= task-name (:onyx/name %)) catalog)]
    (when-not (seq matches)
      (throw (ex-info (format "Couldn't find task %s in catalog" task-name)
                      {:catalog catalog :task-name task-name})))
    (first matches)))

(defn n-peers
  "Takes a workflow and catalog, returns the minimum number of peers
   needed to execute this job."
  [catalog workflow]
  (let [task-set (set (apply concat workflow))]
    (reduce
     (fn [sum t]
       (+ sum (or (:onyx/min-peers (find-task catalog t)) 1)))
     0 task-set)))

;;;; Lifecycles utils ;;;;

(def input-channel-capacity 10000)

(def output-channel-capacity (inc input-channel-capacity))

(def get-input-channel
  (memoize
   (fn [id] (chan input-channel-capacity))))

(def get-output-channel
  (memoize
   (fn [id] (chan (sliding-buffer output-channel-capacity)))))

(defn inject-in-ch [event lifecycle]
  {:core.async/chan (get-input-channel (:core.async/id lifecycle))})

(defn inject-out-ch [event lifecycle]
  {:core.async/chan (get-output-channel (:core.async/id lifecycle))})

(def in-calls
  {:lifecycle/before-task-start inject-in-ch})

(def out-calls
  {:lifecycle/before-task-start inject-out-ch})

;;; Stubs lifecycles to use core.async IO, instead of, say, Kafka or Datomic.


