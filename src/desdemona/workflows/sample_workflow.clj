(ns desdemona.workflows.sample-workflow)

;;; The workflow of an Onyx job describes the graph of all possible
;;; tasks that data can flow between.

(defmulti build-workflow :mode)

(defmethod build-workflow :dev
  [ctx]
  [[:read-lines :extract-line-info]
   [:extract-line-info :prepare-rows]
   [:prepare-rows :write-lines]])

(defmethod build-workflow :prod
  [ctx]
  [[:read-lines :extract-line-info]
   [:extract-line-info :prepare-rows]
   [:prepare-rows :write-lines]])
