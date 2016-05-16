(ns desdemona.ui.table
  (:require [wilson.dom :as wd]
            [reagent.session :as session]
            [dommy.core :as d :refer-macros [sel sel1]]))

(defn toggle-key
  "Adds/removes k in given coll and returns the coll."
  [coll k]
  ((if (contains? coll k)
     disj conj) coll k))

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

(defn sticky-table-header
  "Repeats sorted-table headers for headers affix showing up on scroll."
  [ks]
  [:table {:class "table table-sticky"
           :id "table-sticky"}
   [:thead
    [:tr
     (for [k ks]
       ^{:key (wd/describe-key k)}
       [:th
        (wd/describe-key k)])]]])

(defn get-el-width
  [el]
  (:width (d/bounding-client-rect el)))

(defn get-el-height
  [el]
  (:height (d/bounding-client-rect el)))

(defn set-sticky-table-widths
  "Sets table/ths widths on sticky-table to match those of sorted-table."
  []
  (let [table (sel1 [:#sorted-table-wrapper :table])
        table-sticky (sel1 :#table-sticky)
        sticky-headers (sel [:.table-sticky :th])
        table-headers (sel [:.table-sorted :th])
        page-header-height (get-el-height (sel1 :#page-nav))
        page-header-margin-bot (d/px (sel1 :#page-nav) :margin-bottom)
        page-scroll-y (.-pageYOffset js/window)]
    ;set sticky-table width
    (d/set-style! table-sticky
                  :width
                  (str (get-el-width table) "px"))
    ;set table-headers widths
    (doseq [[sticky-th table-th] (map vector
                                      sticky-headers
                                      table-headers)]
      (d/set-style! sticky-th
                    :width
                    (str (get-el-width table-th) "px")))
    ;set top position
    (d/set-style! table-sticky
                  :top
                  (str (- page-scroll-y
                          page-header-height
                          page-header-margin-bot)
                       "px"))
    ;recur
    (.requestAnimationFrame js/window set-sticky-table-widths)))

(def sticky-header-component
  (with-meta sticky-table-header
    {:component-did-mount #(set-sticky-table-widths)}))

(defn table-component
  []
  (let [state-deref @session/state
        table-wrapper-class
        (str "table-responsive table-sorted after-drawer after-drawer--left"
             (when (:columns-toggler-open? state-deref)
               " drawer-open"))]
    [:div {:class "container-fluid relative"}
     [columns-toggler-component (:all-table-ks state-deref) :table-toggled-ks]
     [:div {:class table-wrapper-class
            :id "sorted-table-wrapper"}
      [wd/sorted-table
       (:table-toggled-ks state-deref)
       (:results state-deref)
       session/state]
      [sticky-header-component (:table-toggled-ks state-deref)]]]))
