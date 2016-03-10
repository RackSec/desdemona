(ns desdemona.ui.nav
  (:require [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [desdemona.ui.dashboard :refer [dashboard-component]]
            [desdemona.ui.table :refer [table-component]]))

(secretary/defroute "/" []
  (session/put! :current-page dashboard-component))

(secretary/defroute "/table" []
  (session/put! :current-page table-component))

(defn nav
  "App navigation bar."
  []
  [:nav
   [:ul
    [:li
     [:a {:href "/"} "Dashboard"]]
    [:li
     [:a {:href "/table"} "Table"]]]])
