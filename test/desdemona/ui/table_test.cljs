(ns desdemona.ui.table-test
  (:require
   [desdemona.ui.table :as table]
   [cljs.test :as t :refer-macros [deftest testing is are]]
   [reagent.core :as r]
   [reagent.session :as session]
   [cljs.core.match :refer-macros [match]]
   [dommy.core :as d :refer-macros [sel sel1]]
   [wilson.dom :as wd]))

(def test-state {:results [{:a 1 :b 2}
                           {:a 4 :b 2}]
                 :table-toggled-ks (sorted-set :a :b)
                 :all-table-ks (sorted-set :a :b)
                 :columns-toggler-open? false})

(def isClient?
  "Are we running in a browser?"
  (some? (try (.-document js/window)
              (catch js/Object e nil))))

(def rflush r/flush)

(defn add-test-div [name]
  (let [body    (sel1 :body)
        div     (d/create-element "div")]
    (d/append! body div)
    div))

(defn with-mounted-component [comp f]
  (when isClient?
    (let [div (add-test-div "_testreagent")]
      (let [comp (r/render-component comp div #(f comp div))]
        (r/unmount-component-at-node div)
        (r/flush)
        (d/remove! (sel1 :body) div)))))

(deftest columns-toggler-component-test
  (session/reset! test-state)
  (with-mounted-component [table/columns-toggler-component
                           (:all-table-ks @session/state)
                           :table-toggled-ks]
    (fn [c div]
      (let [first-li (sel1 div :li)
            first-link (sel1 div [:li :a])
            active? #(d/has-class? % "active")]
        (testing "generated markup"
          (is (= (d/html first-link) "A"))
          (is (active? first-li)))
        (testing "clicks"
          (is (some #{:a} (:table-toggled-ks @session/state)))
          (.click first-link)
          (is (nil? (some #{:a} (:table-toggled-ks @session/state)))))))))

(deftest sticky-header-component-test
  []
  (let [ks (:all-table-ks test-state)
        component (table/sticky-header-component ks)
        [table-class table-id ths] (match component
                                     [:table {:class table-class
                                              :id table-id}
                                      [:thead
                                       [:tr ths]]]
                                     [table-class table-id ths])]
    (is (= table-class "table table-sticky"))
    (is (= table-id "table-sticky"))
    (doseq [[[_ _ desc] k] (map vector ths ks)]
      (is (= desc (wd/describe-key k))))))

(deftest table-component-test
  (let [results-count (count (:results test-state))
        component [table/table-component]]
    (session/reset! test-state)
    (with-mounted-component component
      (fn [c div]
        (let [first-th (.querySelector div "th:first-of-type")
              first-row-entry (.querySelector div "tr:first-of-type > td")
              rows-count (.-length (.querySelectorAll div "tbody tr"))]
          (testing "generated markup"
            (is (= (.-innerHTML first-th) "A"))
            (is (= (.-innerHTML first-row-entry) "1"))
            (is (= rows-count results-count))))))))
