(ns desdemona.workflows.sample-workflow)

;;; The workflow of an Onyx job describes the graph of all possible
;;; tasks that data can flow between.

(defn build-workflow []
  [[:read-lines :original-wrapper]
   [:original-wrapper :determine-origin]
   [:determine-origin :build-row]
   [:build-row :prepare-rows]
   [:prepare-rows :write-lines]])
