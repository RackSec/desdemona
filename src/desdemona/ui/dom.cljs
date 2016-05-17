(ns desdemona.ui.dom
  (:require [reagent.core :as r]
            [cljsjs.react-bootstrap]
            [dommy.core :as d]))

(def button
  (r/adapt-react-class (aget js/ReactBootstrap "Button")))

(def dropdown-button
  (r/adapt-react-class (aget js/ReactBootstrap "DropdownButton")))

(def menu-item
  (r/adapt-react-class (aget js/ReactBootstrap "MenuItem")))

(defn get-el-width
  [el]
  (:width (d/bounding-client-rect el)))

(defn get-el-height
  [el]
  (:height (d/bounding-client-rect el)))
