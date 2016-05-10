(ns desdemona.ui.table-test
  (:require
   [desdemona.ui.table :as table]
   [cljs.test :as t :refer-macros [deftest testing is are]]
   [reagent.core :as r]
   [reagent.session :as session]
   [cljs.core.match :refer-macros [match]]))

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
  (let [doc     js/document
        body    (.-body js/document)
        div     (.createElement doc "div")]
    (.appendChild body div)
    div))

(defn with-mounted-component [comp f]
  (when isClient?
    (let [div (add-test-div "_testreagent")]
      (let [comp (r/render-component comp div #(f comp div))]
        (r/unmount-component-at-node div)
        (r/flush)
        (.removeChild (.-body js/document) div)))))

(deftest columns-toggler-component-test
  (session/reset! test-state)
  (with-mounted-component [table/columns-toggler-component
                           (:all-table-ks @session/state)
                           :table-toggled-ks]
    (fn [c div]
      (let [first-li (.querySelector div "li:first-of-type")
            first-link (.querySelector div "li:first-of-type a")
            active? #(.contains (.-classList %) "active")]
        (testing "generated markup"
          (is (= (.-innerHTML first-link) "A"))
          (is (active? first-li)))
        (testing "clicks"
          (is (some #{:a} (:table-toggled-ks @session/state)))
          (.click first-link)
          (is (nil? (some #{:a} (:table-toggled-ks @session/state)))))))))

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
