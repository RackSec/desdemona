(ns desdemona.ui.core-test
  (:require
   [desdemona.ui.core :as c]
   [cljs.test :as t :refer-macros [deftest testing is are]]))

(deftest add-tests
  (is (= (c/add 1 2) 3)))
