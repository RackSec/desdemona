(ns desdemona.ui.table-test
  (:require
   [desdemona.ui.table :as table]
   [cljs.test :as t :refer-macros [deftest testing is are]]
   [reagent.core :as r]
   [cljs.core.match :refer-macros [match]]))

(deftest table-component-test
  (let [state (r/atom {:results [{:a 1 :b 2}
                                 {:a 4 :b 2}]})
        component (table/table-component state)
        [table wrapper-class] (match component
                                [:div {:class "container-fluid"}
                                 [:div {:class wrapper-class}
                                  table]]
                                [table wrapper-class])]
    (is (= wrapper-class "table-responsive table-sorted"))))
