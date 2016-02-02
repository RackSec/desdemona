(ns desdemona.catalogs.sample-catalog
    (:require [desdemona.functions.sample-functions :refer [format-line upper-case transform-segment-shape prepare-rows]]))

;;; Catalogs describe each task in a workflow. We use
;;; them for describing input and output sources, injecting parameters,
;;; and adjusting performance settings.

(defn build-catalog
  ([] (build-catalog 5 50))
  ([batch-size batch-timeout]
   [
	{:onyx/name :format-line
	 :onyx/fn :desdemona.functions.sample-functions/format-line
	 :onyx/type :function
	 :onyx/batch-size batch-size
	 :onyx/batch-timeout batch-timeout
	 :onyx/doc "Strips the line of any leading or trailing whitespace"}

	{:onyx/name :upper-case
	 :onyx/fn :desdemona.functions.sample-functions/upper-case
	 :onyx/type :function
	 :onyx/batch-size batch-size
	 :onyx/batch-timeout batch-timeout
	 :onyx/doc "Capitalizes the first letter of the line"}

	{:onyx/name :extract-line-info
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
