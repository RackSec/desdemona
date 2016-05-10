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
  "Component with a list of buttons with a click handler to toggle keys in
  given session-entry."
  [ks session-entry]
  (let [state-deref @session/state
        active? #(some #{%} (session-entry state-deref))
        toggler-class (str "columns-toggler drawer drawer--left"
                           (when (:columns-toggler-open? state-deref) " open"))]
    [:div {:class toggler-class}
     [:button {:class "drawer-toggler btn brad-0"
               :on-click
               #(session/update! :columns-toggler-open? not)}]
     [:h3.uppercase.font-medium.color-white.text-center "Toggle columns"]
     (into [:ul.nav.nav-pills.nav-stacked.nav--light]
           (for [k ks]
             [:li {:class (when (active? k) "active")}
              [:a {:href "#"
                   :on-click #(session/update! session-entry toggle-key k)}
               (wd/describe-key k)]]))]))

(defn table-component
  []
  (let [state-deref @session/state
        table-wrapper-class
        (str "table-responsive table-sorted after-drawer after-drawer--left"
             (when (:columns-toggler-open? state-deref)
               " drawer-open"))]
    [:div {:class "container-fluid relative"}
     [columns-toggler-component (:all-table-ks state-deref) :table-toggled-ks]
     [:div {:class table-wrapper-class}
      [wd/sorted-table
       (:table-toggled-ks state-deref)
       (:results state-deref)
       session/state]]]))
