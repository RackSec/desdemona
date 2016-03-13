(ns desdemona.ui.dashboard-test
  (:require
   [desdemona.ui.dashboard :as dashboard]
   [cljs.test :as t :refer-macros [deftest testing is are]]))

(deftest dashboard-component-test
  (is (= (dashboard/dashboard-component)
         [:div [:h2 "Home"]])))
