(ns desdemona.ui.nav-test
  (:require
   [desdemona.ui.nav :as n]
   [desdemona.ui.dom :as d]
   [reagent.session :as session]
   [cljs.test :as t :refer-macros [deftest testing is are]]))

(deftest search-form-component-test
  (is (= (n/search-form-component)
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
             [:i.fa.fa-search {:aria-hidden true}]]]]])))

(deftest nav-test
  (is (= (n/nav)
         [:div {:class "navbar navbar-default navbar-double-row"
                :id "page-nav"}
          [:div.container-fluid.pt-30.pb-30
           [:div.navbar-header
            [:a.navbar-brand.brand-logo {:href "#"}
             "Desdemona"]]
           (n/search-form-component)]
          [:div.container-fluid
           [:nav
            (let [current-page (session/get :current-page)
                  active? #(= % current-page)]
              [:ul.nav.nav-tabs
               [:li {:class nil}
                [:a {:href "/"} "Dashboard"]]
               [:li {:class nil}
                [:a {:href "/table"} "Table"]]])]]])))
