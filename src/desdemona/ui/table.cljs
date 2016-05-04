(ns desdemona.ui.table
  (:require [wilson.dom :as wd]
            [reagent.session :as session]))

(defn table-component
  [state]
  (let [state-deref @state
        rows (:results state-deref)
        all-keys (-> (mapcat wd/get-all-keys rows) distinct wd/prepare-keys)]
    (wd/sorted-table all-keys rows session/state)))
