(ns desdemona.ui.nav
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [desdemona.ui.dashboard :refer [dashboard-component]]
            [desdemona.ui.table :refer [table-component]]
            [desdemona.ui.dom :as d]))

(secretary/defroute "/" []
  (session/put! :current-page dashboard-component))

(secretary/defroute "/table" []
  (session/put! :current-page table-component))

(defn ^:private search-form-component
  []
  [:form.navbar-form.navbar-left
   [:div.input-group
    [:input.main-input.form-control
     {:placeholder "Search..."}]
    [:div.input-group-btn
     [d/dropdown-button {:id "timeframe-dropdown"
                         :bsStyle "default"
                         :pullRight true
                         :title "last 24h"}
      [d/menu-item {} "Last 4h"]
      [d/menu-item {} "Last 8h"]
      [d/menu-item {} "Last 12h"]
      [d/menu-item {} "Last week"]
      [d/menu-item {} "Last month"]
      [d/menu-item {:divider true}]
      [d/menu-item {} "Custom"]]
     [:button.btn.btn-primary {:aria-label "Search"}
      [:i.fa.fa-search {:aria-hidden true}]]]]])

(defn nav
  "App navigation bar."
  []
  [:div.navbar.navbar-default.navbar-double-row
   [:div.container-fluid.pt-30.pb-30
    [:div.navbar-header
     [:a.navbar-brand.brand-logo {:href "#"}
      "Desdemona"]]
    (search-form-component)]
   [:div.container-fluid
    [:nav
     (let [current-page (session/get :current-page)
           active? #(= % current-page)]
       [:ul.nav.nav-tabs
        [:li {:class (when (active? dashboard-component) "active")}
         [:a {:href "/"} "Dashboard"]]
        [:li {:class (when (active? table-component) "active")}
         [:a {:href "/table"} "Table"]]])]]])
