(ns desdemona.cli-test
  (:require
   [desdemona.launcher.aeron-media-driver :as aeron]
   [desdemona.launcher.launch-prod-peers :as peers]
   [desdemona.launcher.utils :as utils]
   [clojure.test :refer [deftest testing is]]
   [clojure.string :as s]
   [clojure.core.async :as a]
   [taoensso.timbre :refer [spy info]])
  (:import
   [java.io StringWriter]))

(deftest block-forever!-tests
  (let [ch (a/chan)]
    (with-redefs [a/chan (fn []
                           (a/put! ch ::blocked-forever)
                           ch)]
      (is (= (utils/block-forever!) ::blocked-forever)))))

(def expected-env-config
  {:onyx/id nil ;; from env
   :onyx.bookkeeper/server? true
   :onyx.bookkeeper/delete-server-data? true
   :zookeeper/address "zk:2181"
   :zookeeper/server? false
   :zookeeper.server/port 2181})

(def expected-peer-config
  {:onyx/id nil ;; from env

   :onyx.messaging/impl :aeron
   :onyx.messaging/allow-short-circuit? true
   :onyx.messaging.aeron/embedded-driver? false
   :onyx.messaging/bind-addr "localhost"
   :onyx.messaging/peer-port 40200

   :zookeeper/address "zk:2181"

   :onyx.peer/job-scheduler :onyx.job-scheduler/greedy
   :onyx.peer/zookeeper-timeout 60000})

(def read-config!-tests
  (is (= (utils/read-config!)
         {:env-config expected-env-config
          :peer-config expected-peer-config})))

(def fake-block-forever!
  (constantly ::blocked-forever))

(defn fake-exit
  [status]
  [::exited status])

(defmacro with-fake-launcher-side-effects
  "Runs body with a fake exit, block-forever! and stdout.

  This assumes you're using com.gfredericks.system-slash-exit, because
  it's _way_ easier to mock out than the alternative.

  Returns [body-value stdout]. If it would have blocked forever, body-value
  will be ::blocked-forever. If it would have exited, body-value will
  be [::exited status]."
  [& body]
  `(with-redefs [com.gfredericks.system-slash-exit/exit fake-exit
                 desdemona.launcher.utils/block-forever! fake-block-forever!]
     (let [stdout# (StringWriter.)]
       (binding [*out* stdout#]
         (let [result# (do ~@body)]
           [result# (str stdout#)])))))

(def ^:private usage-lines
  ["Usage:"
   ""
   "  -d, --delete-dirs  Delete the media drivers directory on startup"
   "  -h, --help         Display a help message"
   ""])

(def ^:private error-lines
  ["Unknown option: \"--xyzzy\""])

(deftest aeron-main-tests
  (testing "--help displays usage"
    (let [[result stdout] (with-fake-launcher-side-effects
                            (aeron/-main "--help"))]
      (is (= result [::exited 0]))
      (is (= stdout (s/join \newline usage-lines)))))
  (testing "bogus parameters display errors + usage"
    (let [[result stdout] (with-fake-launcher-side-effects
                            (aeron/-main "--xyzzy"))]
      (is (= result [::exited 1]))
      (is (= stdout (s/join \newline (concat error-lines usage-lines))))))
  (testing "regular run"
    (let [launched (atom false)]
      (with-redefs [aeron/launch-media-driver!
                    (fn [ctx]
                      (is (not (.dirsDeleteOnStart ctx)))
                      (reset! launched true))]
        (let [[result stdout] (with-fake-launcher-side-effects (aeron/-main))]
          (is @launched)
          (is (= result ::blocked-forever))
          (is (= stdout "Launched the Media Driver. Blocking forever...\n"))))))
  (testing "regular run with deleted directories"
    (let [launched (atom false)]
      (with-redefs [aeron/launch-media-driver!
                    (fn [ctx]
                      (is (.dirsDeleteOnStart ctx))
                      (reset! launched true))]
        (let [[result stdout] (with-fake-launcher-side-effects
                                (aeron/-main "-d"))]
          (is @launched)
          (is (= result ::blocked-forever))
          (is (= stdout "Launched the Media Driver. Blocking forever...\n"))))))
  (testing "bail with exception"
    (let [launched (atom false)]
      (with-redefs [aeron/launch-media-driver!
                    (fn [ctx] (throw (IllegalStateException. "yolo")))]
        (is (thrown-with-msg?
             Exception (re-pattern @#'aeron/aeron-launch-error-message)
             (with-fake-launcher-side-effects (aeron/-main))))))))

(deftest peers-main-tests
  (testing "first argument must be an integer"
    (is (thrown-with-msg?
         NumberFormatException #"BOGUS"
         (peers/-main "BOGUS"))))
  (testing "happy case"
    (let [n-peers 6
          group (gensym)
          peers (for [_ (range n-peers)] (gensym))
          events (atom [])
          redef-pairs (for [sym ['onyx.api/start-peer-group
                                 'onyx.api/start-env
                                 'onyx.api/start-peers
                                 'desdemona.launcher.utils/add-shutdown-hook!
                                 'onyx.api/shutdown-peer
                                 'onyx.api/shutdown-peers
                                 'onyx.api/shutdown-peer-group
                                 'clojure.core/shutdown-agents]]
                        [(resolve sym)
                         (fn [& args]
                           (let [event (into [sym] args)]
                             (swap! events conj event)))])
          redefs (into {} redef-pairs)]
      (with-redefs-fn redefs
        (fn []
          (is (= [] @events))
          (let [[result stdout] (with-fake-launcher-side-effects
                                  (peers/-main (str n-peers)))]
            (info "AFTER" @events)
            (is (= result ::blocked-forever))
            (is (= stdout (s/join \newline
                                  ["Connecting to Zookeeper:  zk:2181"
                                   "Started peers. Blocking forever."
                                   ""])))))))))
