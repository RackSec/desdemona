(ns desdemona.functions.sample-functions
  (:require
   [clojure.core.match :refer [match]]))

;;; Defines functions to be used by the peers. These are located
;;; with fully qualified namespaced keywords, such as
;;; desdemona.functions.sample-functions/add-message-origin

(defn build-row [segment]
  {:line (str (name (segment :origin)) ": " (segment "MESSAGE"))})

(defn prepare-rows [segment]
  {:rows [segment]})

(defn message-origin [segment]
  (match [segment]
    [{"_parsed" {"metadata" {"customerIDString" _}}}] :falconhose
    [{"_parsed" {"id" _ "type" _ "critical" _ "message" _}}] :cloudpassage
    [{"_parsed" _}] :json
    :else :syslog))

(defn add-message-origin [segment]
  (assoc-in segment [:origin] (message-origin segment)))
