(ns desdemona.workflows.sample-workflow)

;;; The workflow of an Onyx job describes the graph of all possible
;;; tasks that data can flow between.

(def workflow
  [[:read-lines :format-line]
   [:format-line :upper-case]
   [:upper-case :write-lines]])
