(ns desdemona.ui.server
  (:require
   [ring.middleware.defaults :refer :all]
   [hiccup.page :refer [html5 include-js include-css]]
   [hiccup.element :refer [javascript-tag]]))

(def page
  (html5
   [:html
    [:head
     [:title "Desdemona"]
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     (include-css "/css/main.css")]
    [:body
     [:div#app]
     (include-js "/js/main.js")]]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body page})

(def spa-server
  (wrap-defaults handler site-defaults))
