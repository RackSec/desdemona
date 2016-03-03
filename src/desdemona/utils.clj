(ns desdemona.utils
  (:require [clojure.core.async :refer [chan sliding-buffer >!!]]
            [clojure.java.io :refer [resource]]
            [clojure.string :as string]))

;;;; Test utils ;;;;

(defn find-task
  "Finds the catalog entry where the :onyx/name key equals task-name"
  [catalog task-name]
  (let [matches (filter #(= task-name (:onyx/name %)) catalog)]
    (when-not (seq matches)
      (throw (ex-info (format "Couldn't find task %s in catalog" task-name)
                      {:catalog catalog :task-name task-name})))
    (first matches)))

(defn str->kw
  "Turns a string into a keyword"
  [s]
  (-> s
      name
      (string/lower-case)
      (string/replace #"[_ ]" "-")
      (string/replace #"[.]" "")
      (string/replace #"^-+" "")
      keyword))

(defn kwify-map
  "Turns a map with str keys into one with kw keys.
  Unlike clojure.walk/keywordize-keys, turns spaces into dashes, and
  only works on one level."
  [m]
  (let [kvs (map (fn [[k v]] [(str->kw k) v]) m)]
    (into (empty m) kvs)))
