(ns desdemona.plugins.swift
  (:require
   [onyx.peer.pipeline-extensions :as p-ext]
   [onyx.peer.function :as function]
   [taoensso.timbre :refer [info]]
   [cheshire.core :as json]
   [aleph.http :as http]
   [manifold.deferred :as md]
   [clj-time.core :as t]
   [clj-time.format :as f]
   [byte-streams :as bs]
   [camel-snake-kebab.core :refer [->kebab-case-keyword]]))

(defn ^:private find-first
  "Get the first item from the given collection for which the given function
  returns true."
  [f coll]
  (first (filter f coll)))

(defn get-cloud-files
  "Reach into an authentication response and extract the Cloud Files data from
  the service catalog."
  [auth-response]
  (find-first #(and (= "cloudFiles" (:name %)) (= "object-store" (:type %)))
              (-> auth-response :access :service-catalog)))

(defn get-token
  "Reach into an authentication response and extract the auth-related part
  of it (which will include both the auth token and the expiration date)."
  [auth-response]
  (-> auth-response :access :token))

(defn authenticate!
  "Authenticate with Cloud Files and return the authentication response. This
  response will include an auth token that can be used for subsequent
  requests, as well as a service catalog that will indicate the URL to use
  for, for example, Cloud Files."
  [auth-url username api-key]
  (let [body (json/generate-string {"auth" {"RAX-KSKEY:apiKeyCredentials"
                                            {"username" username
                                             "apiKey" api-key}}})]
    @(md/chain (http/post auth-url {:body body
                                    :content-type :json
                                    :accept :json
                                    :throw-entire-message true
                                    :as :stream})
               :body
               bs/to-reader
               #(json/decode-stream % ->kebab-case-keyword))))

(defn create-container!
  "Create a container on Cloud Files. This requires an auth token, which is
  passed as a request header. If the container already exists, nothing
  happens."
  [cf-url auth-token container-name]
  (let [url (str cf-url "/" container-name)]
    @(md/chain (http/put url {:accept :json
                              :throw-entire-message true
                              :headers {"X-Auth-Token" auth-token}})
               :status)))

(defn write-file!
  "Encode the given contents to JSON and write them to the given file on
  Cloud Files, at the given URL. This requires an auth token, which is passed
  as a request header. If the file already exists, it is overwritten."
  [cf-url auth-token container-name file-name contents]
  (let [url (str cf-url "/" container-name "/" file-name)
        body (json/generate-string contents)]
    @(md/chain (http/put url {:body body
                              :content-type :json
                              :throw-entire-message true
                              :headers {"X-Auth-Token" auth-token}})
               :status)))

(defn calculate-container-name
  "We create one container per day, named based on the date. This function
  generates a container name indicating today's date."
  []
  (f/unparse (f/formatter "yyyy-MM-dd") (t/now)))

(defn calculate-file-name
  "We create one file per batch, named based on the current time. The
  resolution is intended to be small enough such that each filename will be
  unique per batch."
  []
  (f/unparse (f/formatter "HH:mm:ss-SSS") (t/now)))

(defrecord SwiftWriteRows
           [auth-url username api-key]
  p-ext/Pipeline

  (read-batch
    [_ event]
    (function/read-batch event))

  (write-batch
    [_ {:keys [onyx.core/results]}]
    (when-let [segments (seq (map :message (mapcat :leaves (:tree results))))]
      (let [auth-response (authenticate! auth-url username api-key)
            auth-token (-> auth-response get-token :id)
            cloud-files-url (-> auth-response get-cloud-files :endpoints
                                first :public-url)
            container-name (calculate-container-name)
            file-name (calculate-file-name)]
        (info "Writing" (count segments) "segments to"
              container-name file-name)
        (create-container! cloud-files-url auth-token container-name)
        (write-file! cloud-files-url auth-token container-name file-name
                     segments)))
    {:onyx.core/written? true})

  (seal-resource
    [_ _]
    {}))

(defn write-batch
  "This is the function that gets added to the catalog, and sets up the plugin
  that actually writes batches to Cloud Files."
  [pipeline-data]
  (let [task-map (:onyx.core/task-map pipeline-data)
        {auth-url :swift/auth-url
         username :swift/username
         api-key :swift/api-key} task-map]
    (->SwiftWriteRows auth-url username api-key)))
