(ns desdemona.functions.sample-functions
  (:require
   [clojure.core.match :refer [match]]))

(defn add-original-wrapper
  [segment]
  {:original segment})

(defn message-origin
  [message]
  (match [message]
    [{:parsed {:metadata {:customer-id-string _}}}] :falconhose
    [{:parsed {:id _ :type _ :critical _ :message _}}] :cloudpassage
    [{:parsed _}] :json
    :else :syslog))

(defn add-message-origin
  [segment]
  (assoc segment :origin (message-origin (:original segment))))
