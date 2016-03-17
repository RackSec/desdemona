(ns desdemona.ui.server-test
  (:require [clojure.test :refer [deftest is]]
            [desdemona.ui.server :as s]
            [ring.mock.request :refer [request]]))

(deftest handler-test
  (let [response (s/handler (request :get "/"))
        second-response (s/handler (request :get "/blah"))]
    (is (= (:status response) 200))
    (is (= (get-in response [:headers "Content-Type"]) "text/html"))
    (is (= (:status second-response) 200))
    (is (= (get-in response [:headers "Content-Type"]) "text/html"))))
