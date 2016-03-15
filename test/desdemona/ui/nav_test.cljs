(ns desdemona.ui.nav-test
  (:require
   [desdemona.ui.nav :as n]
   [cljs.test :as t :refer-macros [deftest testing is are]]))

(deftest nav-test
  (is (= (n/nav)
         [:nav
          [:ul
           [:li
            [:a {:href "/"} "Dashboard"]]
           [:li
            [:a {:href "/table"} "Table"]]]])))
