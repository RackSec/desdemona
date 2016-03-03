(ns desdemona.catalogs.sample-catalog)

;;; Catalogs describe each task in a workflow. We use
;;; them for describing input and output sources, injecting parameters,
;;; and adjusting performance settings.

(defn build-catalog
  [batch-size batch-timeout]
  [{:onyx/name :original-wrapper
    :onyx/fn :desdemona.functions.sample-functions/add-original-wrapper
    :onyx/type :function
    :onyx/batch-size batch-size
    :onyx/batch-timeout batch-timeout
    :onyx/doc "Wrap the original message before further processing"}

   {:onyx/name :determine-origin
    :onyx/fn :desdemona.functions.sample-functions/add-message-origin
    :onyx/type :function
    :onyx/batch-size batch-size
    :onyx/batch-timeout batch-timeout
    :onyx/doc "Determine the origin of the message"}

   {:onyx/name :build-row
    :onyx/fn :desdemona.functions.sample-functions/build-row
    :onyx/type :function
    :onyx/batch-size batch-size
    :onyx/batch-timeout batch-timeout
    :onyx/doc "Transform the segment into a row for MySQL"}

   {:onyx/name :prepare-rows
    :onyx/fn :desdemona.functions.sample-functions/prepare-rows
    :onyx/type :function
    :onyx/batch-size batch-size
    :onyx/batch-timeout batch-timeout}])
