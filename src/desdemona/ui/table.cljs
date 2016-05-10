(ns desdemona.ui.table
  (:require [wilson.dom :as wd]
            [reagent.session :as session]))

(defn toggle-key
  "Adds/removes k in given coll and returns the coll."
  [coll k]
  (if (contains? coll k)
   (disj coll k)
   (conj coll k)))

(defn columns-toggler-component
  []
  (let [ks (:all-table-ks @session/state)]
    (into [:ul.nav.nav-pills.nav-stacked]
     (for [k ks]
      [:li
       [:a {:href "#"
            :on-click #(session/update! :table-toggled-ks toggle-key k)}
        (wd/describe-key k)]]))))

(defn table-component
  []
  (let [state-deref @session/state]
    [:div {:class "container-fluid"}
     [columns-toggler-component]
     [:div {:class "table-responsive table-sorted"}
      [wd/sorted-table
       (:table-toggled-ks state-deref)
       (:results state-deref)
       session/state]]]))
