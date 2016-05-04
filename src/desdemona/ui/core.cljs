(ns desdemona.ui.core
  (:require [desdemona.ui.nav :refer [nav]]
            [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

(defn current-page []
  [:div
   (nav)
   [(session/get :current-page)]])

(defn mount-root []
  (r/render [current-page] (.getElementById js/document "app")))

(defn init!
  []
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (secretary/dispatch! path))
    :path-exists?
    (fn [path]
      (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
