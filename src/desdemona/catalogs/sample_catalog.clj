(ns desdemona.catalogs.sample-catalog
  (:require [desdemona.functions.sample-functions :refer [transform-segment-shape prepare-rows]]))

;;; Catalogs describe each task in a workflow. We use
;;; them for describing input and output sources, injecting parameters,
;;; and adjusting performance settings.

(defn build-catalog
  ([] (build-catalog 5 50))
  ([batch-size batch-timeout]
   [{:onyx/name :extract-line-info
     :onyx/fn :desdemona.functions.sample-functions/transform-segment-shape
     :onyx/type :function
     :onyx/batch-size batch-size
     :onyx/batch-timeout batch-timeout
     :keypath {"line" [:line]}
     :onyx/params [:keypath]
     :onyx/doc "Extracts the line"}

    {:onyx/name :prepare-rows
     :onyx/fn :desdemona.functions.sample-functions/prepare-rows
     :onyx/type :function
     :onyx/batch-size batch-size
     :onyx/batch-timeout batch-timeout}]))
