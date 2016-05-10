(ns desdemona.ui.table-test
  (:require
   [desdemona.ui.table :as table]
   [cljs.test :as t :refer-macros [deftest testing is are]]
   [reagent.core :as r]
   [reagent.session :as session]
   [cljs.core.match :refer-macros [match]]))

(def isClient (not (nil? (try (.-document js/window)
                              (catch js/Object e nil)))))

(def rflush r/flush)

(defn add-test-div [name]
  (let [doc     js/document
        body    (.-body js/document)
        div     (.createElement doc "div")]
    (.appendChild body div)
    div))

(defn with-mounted-component [comp f]
  (when isClient
    (let [div (add-test-div "_testreagent")]
      (let [comp (r/render-component comp div #(f comp div))]
        (r/unmount-component-at-node div)
        (r/flush)
        (.removeChild (.-body js/document) div)))))

(deftest table-component-test
  (let [state {:results [{:a 1 :b 2}
                         {:a 4 :b 2}]
               :table-toggled-ks [:a :b]
               :all-table-ks [:a :b]
               :columns-toggler-open? false}
        results-count (count (:results state))
        component [table/table-component]]
    (session/swap! merge state)
    (with-mounted-component component
      (fn [c div]
        (let [first-th (.querySelector div "th:first-of-type")
              first-row-entry (.querySelector div "tr:first-of-type > td")
              rows-count (.-length (.querySelectorAll div "tbody tr"))]
          (testing "generated markup"
            (is (= (.-innerHTML first-th) "A"))
            (is (= (.-innerHTML first-row-entry) "1"))
            (is (= rows-count results-count))))))))
