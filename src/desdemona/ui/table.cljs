(ns desdemona.ui.table
  (:require [wilson.dom :as wd]
            [reagent.session :as session]))

(defn table-component
  []
  (let [state-deref @session/state]
    [:div {:class "container-fluid"}
     [:div {:class "table-responsive table-sorted"}
      [wd/sorted-table
       (:table-toggled-ks state-deref)
       (:results state-deref)
       session/state]]]))
