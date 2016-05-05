(ns desdemona.ui.table
  (:require [wilson.dom :as wd]
            [reagent.session :as session]))

(defn table-component
  []
  (let [rows (:results @session/state)
        all-keys (-> (mapcat wd/get-all-keys rows) distinct wd/prepare-keys)]
    [:div {:class "container-fluid"}
     [:div {:class "table-responsive table-sorted"}
      [wd/sorted-table all-keys rows session/state]]]))
