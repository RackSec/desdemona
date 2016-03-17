(ns desdemona.ui.server-test
  (:require [clojure.test :refer [deftest is]]
            [desdemona.ui.server :as s]
            [ring.mock.request :refer [request]]))

(deftest page-test
  (is (= s/page
         "<!DOCTYPE html>\n<html><html><head><title>Desdemona</title><meta charset=\"utf-8\"><meta content=\"width=device-width, initial-scale=1\" name=\"viewport\"><link href=\"/css/main.css\" rel=\"stylesheet\" type=\"text/css\"></head><body><div id=\"app\"></div><script src=\"/js/main.js\" type=\"text/javascript\"></script></body></html></html>")))

(deftest handler-test
  (let [response (s/handler (request :get "/"))
        second-response (s/handler (request :get "/blah"))]
    (is (= (:status response) 200))
    (is (= (get-in response [:headers "Content-Type"]) "text/html"))
    (is (= (:status second-response) 200))
    (is (= (get-in response [:headers "Content-Type"]) "text/html"))))
