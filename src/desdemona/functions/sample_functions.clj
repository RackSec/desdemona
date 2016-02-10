(ns desdemona.functions.sample-functions
  (:require [clojure
             [walk :refer [postwalk]]]))

;;; Defines functions to be used by the peers. These are located
;;; with fully qualified namespaced keywords, such as
;;; desdemona.functions.sample-functions/format-line

(defn transform-segment-shape
  "Recursively restructures a segment {:new-key [paths...]}"
  [paths segment]
  (try (let [f (fn [[k v]]
                 (if (vector? v)
                   [k (get-in segment v)]
                   [k v]))]
         (postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) paths))
       (catch Exception e
         segment)))

(defn prepare-rows [segment]
  {:rows [segment]})
