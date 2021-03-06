(ns desdemona.ui.table
  (:require [wilson.dom :as wd]
            [reagent.session :as session]
            [dommy.core :as d :refer-macros [sel sel1]]
            [desdemona.ui.dom :as dd :refer [get-el-height get-el-width]]
            [reagent.core :as r]))

(def sort-order-id :table-sort-order)
(def sort-key-id :table-sort-key)

(defn toggle-key
  "Adds/removes k in given coll and returns the coll."
  [coll k]
  ((if (contains? coll k)
     disj conj) coll k))

(defn make-toggler-affix
  []
  (let [toggler (sel1 :#columns-toggler)
        page-nav (sel1 :#page-nav)
        page-scroll-y (.-pageYOffset js/window)]
    (when toggler
      ((if (> page-scroll-y (get-el-height page-nav))
         d/add-class!
         d/remove-class!)
       toggler "has-affix")
      (r/next-tick make-toggler-affix))))

(defn columns-toggler
  "Component with a list of buttons with a click handler to toggle keys in
  given session-entry."
  [ks session-entry]
  (let [state-deref @session/state
        toggled-cols (session-entry state-deref)
        active? #(some #{%} toggled-cols)
        toggler-class (str "columns-toggler drawer drawer--left"
                           (when (:columns-toggler-open? state-deref)
                             " open"))]
    [:div {:class toggler-class
           :id "columns-toggler"}
     [:div {:class "toggler-body"}
      [dd/overlay-trigger {:placement :bottom
                           :overlay (r/as-component
                                     [dd/tooltip {:id "cols-toggler-trigger"}
                                      "Toggle columns"])}
       [:button {:class "drawer-toggler btn brad-0"
                 :on-click
                 #(session/update! :columns-toggler-open? not)}]]
      [:h3.uppercase.font-medium.color-white.text-center "Toggle columns"]
      (into [:ul.nav.nav-pills.nav-stacked.nav--light]
            (for [k ks]
              [:li {:class (when (active? k) "active")}
               [:a {:href "#"
                    :on-click #(when (> (count toggled-cols) 1)
                                 (session/update! session-entry toggle-key k))}
                (wd/describe-key k)]]))]]))

(def columns-toggler-component
  (with-meta columns-toggler
    {:component-did-mount #(r/next-tick make-toggler-affix)}))

(defn set-sticky-table-widths
  "Sets table/ths widths on sticky-table to match those of sorted-table."
  []
  (let [table (sel1 [:#sorted-table-wrapper :table])
        table-sticky (sel1 :#table-sticky)
        sticky-headers (sel [:.table-sticky :th])
        table-headers (sel [:.table-sorted :th])
        page-nav (sel1 :#page-nav)
        page-scroll-y (.-pageYOffset js/window)]
    (when (pos? (count sticky-headers))
      ; change opacity
      (if (> page-scroll-y (+ (get-el-height page-nav) 50))
        (d/set-style! table-sticky :opacity 1)
        (d/set-style! table-sticky :opacity 0))
      ;set sticky-table width
      (d/set-style! table-sticky
                    :width (str (get-el-width table) "px"))
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
                            (get-el-height page-nav)
                            (d/px page-nav :margin-bottom))
                         "px"))
      (r/next-tick set-sticky-table-widths))))

(defn sticky-table-header
  "Repeats sorted-table headers for headers affix showing up on scroll."
  [ks]
  (let [state-deref @session/state]
    [:table {:class "table table-sticky"
             :id "table-sticky"}
     [:thead
      [:tr
       (map-indexed
        (fn [i k]
          ^{:key (wd/describe-key k)}
          [:th {:class (when (= k (sort-key-id state-deref))
                         (name (sort-order-id state-deref)))
                :on-click (fn []
                            (let [ths (sel [:.table-sorted :th])]
                              (.click (nth ths i))))}
           (wd/describe-key k)])
        ks)]]]))

(def sticky-header-component
  (with-meta sticky-table-header
    {:component-did-mount #(r/next-tick set-sticky-table-widths)}))

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
       session/state
       {:sort-order-id sort-order-id
        :sort-key-id sort-key-id}]
      [sticky-header-component (:table-toggled-ks state-deref)]]]))
