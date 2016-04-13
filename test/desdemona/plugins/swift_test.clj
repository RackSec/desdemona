(ns desdemona.plugins.swift-test
  (:require [clojure.test :refer [deftest is]]
            [cheshire.core :as json]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [clojure.java.io :as io]
            [clj-time.core :as t]
            [clj-http.fake :as http]
            [desdemona.plugins.swift :as swift])
  (:import (org.joda.time DateTimeUtils)))

;(deftest find-first-test
;  (let [f #(= 3 %)
;        coll [1 2 3 4 5]
;        expected 3
;        got (swift/find-first f coll)]
;    (is (= expected got))))

(defmacro freeze-time [time & forms]
  `(try
     (DateTimeUtils/setCurrentMillisFixed (.getMillis ~time))
     ~@forms
     (finally
       (DateTimeUtils/setCurrentMillisSystem))))

(deftest get-cloud-files-test
  (let [cloud-files {:name "cloudFiles" :type "object-store" :id 1}
        auth-response {:access {:service-catalog [{:name "one"}
                                                  {:name "two" :type "object-store"}
                                                  {:name "cloudFiles" :type "something"}
                                                  cloud-files
                                                  {:type "object-store"}]}}
        got (swift/get-cloud-files auth-response)]
    (is (= cloud-files got))))

(deftest get-token-test
  (let [token {:id 1}
        auth-response {:access {:token token}}
        got (swift/get-token auth-response)]
    (is (= token got))))

(deftest authenticate-test
  "This test verifies that the authenticate function makes a request to the
  URL specified by the auth-url parameter, and includes the username and api-key
  in the expected JSON format."
  (http/with-fake-routes
    {"http://authurl.com" {:post (fn [req]
                                   (let [body (json/parse-stream (io/reader (req :body)) ->kebab-case-keyword)
                                         username (-> body :auth :rax-kskey:api-key-credentials :username)
                                         api-key (-> body :auth :rax-kskey:api-key-credentials :api-key)]
                                     {:status 200
                                      :headers {}
                                      :body (json/generate-string {:username username
                                                                   :api-key api-key})}))}}
    (let [result (swift/authenticate "http://authurl.com" "username" "apikey")]
      (is (= {:username "username" :api-key "apikey"} result)))))

(deftest create-container-test
  "This test verifies that the create-container function makes a request to the
  URL specified, with the container-name in the path, and includes the given
  auth token as a header."
  (http/with-fake-routes
    {"http://cf-url.com/containername" {:put (fn [req]
                                               (let [token (-> req :headers (get "X-Auth-Token"))]
                                                 (is (= "authtoken" token))
                                                 {:status 200
                                                  :headers {}
                                                  :body ""}))}}
    (let [result (swift/create-container "http://cf-url.com" "authtoken" "containername")]
      (is (= 200 result)))))

(deftest write-file-test
  "This test verifies that the write-file function makes a request to the URL
  specified, with the container-name and file-name in the path, that it includes
  the auth token as a header, and encodes the given contents as JSON."
  (http/with-fake-routes
    {"http://cf-url.com/containername/filename" {:put (fn [req]
                                                        (let [token (-> req :headers (get "X-Auth-Token"))
                                                              body (json/parse-stream (io/reader (req :body)) ->kebab-case-keyword)]
                                                          (is (= "authtoken" token))
                                                          (is (= {:contents 123} body))
                                                          {:status 200
                                                           :headers {}
                                                           :body ""}))}}
    (let [result (swift/write-file "http://cf-url.com" "authtoken" "containername" "filename" {:contents 123})]
      (is (= 200 result)))))

(deftest calculate-container-name-test
  (freeze-time (t/date-time 2016 6 8 4 15 45 401)
               (let [container-name (swift/calculate-container-name)]
                 (is (= "2016-06-08" container-name)))))

(deftest calculate-file-name-test
  (freeze-time (t/date-time 2016 6 8 14 15 45 401)
               (let [file-name (swift/calculate-file-name)]
                 (is (= "14:15:45-401" file-name)))))

(deftest write-batch-test
  (let [pipeline-data {:onyx.core/task-map {:swift/auth-url "authurl"
                                            :swift/username "username"
                                            :swift/api-key "apikey"}}
        record (swift/write-batch pipeline-data)]
    (is (= "authurl" (.auth-url record)))
    (is (= "username" (.username record)))
    (is (= "apikey" (.api-key record)))))
