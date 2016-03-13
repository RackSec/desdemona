(ns desdemona.ui.table-test
  (:require
   [desdemona.ui.table :as table]
   [cljs.test :as t :refer-macros [deftest testing is are]]))

(deftest table-component-test
  (is (= (table/table-component)
         [:div [:h2 "Table"]])))
