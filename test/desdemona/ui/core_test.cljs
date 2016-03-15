(ns desdemona.ui.core-test
  (:require
   [reagent.core :as reagent :refer [atom]]
   [desdemona.ui.core :as c]
   [cljs.test :as t :refer-macros [deftest testing is are]]))

(def browser?
  "Are we running in a browser?"
  (some? (try (.-document js/window)
              (catch js/Object e nil))))

(defn ^:private add-test-div
  []
  (let [doc js/document
        body (.-body js/document)
        div (.createElement doc "div")]
    (.appendChild body div)
    div))

(defn with-mounted-component
  [comp f]
  (when browser?
    (let [div (add-test-div)]
      (let [comp (reagent/render-component comp div #(f comp div))]
        (reagent/unmount-component-at-node div)
        (reagent/flush)
        (.removeChild (.-body js/document) div)))))
