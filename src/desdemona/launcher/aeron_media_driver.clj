(ns desdemona.launcher.aeron-media-driver
  (:gen-class)
  (:require
   [desdemona.launcher.utils :as utils]
   [clojure.tools.cli :refer [parse-opts]]
   [clojure.string :as s]
   [com.gfredericks.system-slash-exit :as system-slash-exit])
  (:import
   [uk.co.real_logic.aeron.driver MediaDriver MediaDriver$Context]))

(def cli-options
  [["-d" "--delete-dirs"
    "Delete the media drivers directory on startup"
    :default false]
   ["-h" "--help"
    "Display a help message"]])

(def ^:private aeron-launch-error-message
  (str
   "Error starting media driver. This may be due to a media driver data "
   "incompatibility between versions. Check that no other media driver "
   "has been started and then use -d to delete the directory on startup"))

(defn ^:private launch-media-driver!
  "Calls MediaDriver/launch.

  This only exists so the var can be mocked out."
  [ctx]
  (MediaDriver/launch ctx))

(defn ^:private run-media-driver!
  [options]
  (let [ctx (doto (MediaDriver$Context.)
              (.dirsDeleteOnStart (options :delete-dirs)))]
    (try (launch-media-driver! ctx)
         (catch IllegalStateException e
           (throw (Exception. aeron-launch-error-message e))))))

(defn ^:private run-media-driver-and-block!
  [options]
  (run-media-driver! options)
  (println "Launched the Media Driver. Blocking forever...")
  (utils/block-forever!))

(defn ^:private usage
  "Given usage summary, returns lines suitable to print as a usage summary."
  [summary]
  (conj ["Usage:" ""] summary))

(defn ^:private exit!
  "Prints lines to *out* and exit with status."
  [status lines]
  (println (s/join \newline lines))
  (system-slash-exit/exit status))

(defn -main [& args]
  (let [{:keys [options errors summary]} (parse-opts args cli-options)]
    (cond
      (options :help) (exit! 0 (usage summary))
      errors (exit! 1 (into errors (usage summary)))
      :else (run-media-driver-and-block! options))))
