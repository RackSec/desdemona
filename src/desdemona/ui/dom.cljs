(ns desdemona.ui.dom
  (:require [reagent.core :as r]
            [cljsjs.react-bootstrap]))

(def button
  (r/adapt-react-class (aget js/ReactBootstrap "Button")))

(def dropdown-button
  (r/adapt-react-class (aget js/ReactBootstrap "DropdownButton")))

(def menu-item
  (r/adapt-react-class (aget js/ReactBootstrap "MenuItem")))
