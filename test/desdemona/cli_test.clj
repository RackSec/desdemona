(ns desdemona.cli-test
  (:require
   [desdemona.launcher.aeron-media-driver :as aeron]
   [clojure.test :refer [deftest testing is]]
   [clojure.string :as s])
  (:import
   [java.io StringWriter]))

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
         (let [result# ~@body]
           [result# (str stdout#)])))))

(def ^:private usage-lines
  ["Usage:"
   ""
   "  -d, --delete-dirs  Delete the media drivers directory on startup"
   "  -h, --help         Display a help message\n"])

(deftest aeron-main-tests
  "Tests for the aeron-media-driver main."
  (testing "usage"
    (let [[result stdout] (with-fake-launcher-side-effects
                            (aeron/-main "--help"))]
      (is (= result [::exited 0]))
      (is (= stdout (s/join \newline usage-lines))))))
