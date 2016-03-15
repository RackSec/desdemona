(ns desdemona.launcher.aeron-media-driver
  (:gen-class)
  (:require
   [clojure.core.async :refer [chan <!!]]
   [clojure.tools.cli :refer [parse-opts]])
  (:import
   [uk.co.real_logic.aeron Aeron$Context]
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

(defn ^:private run-media-driver!
  [opts]
  (let [ctx (doto (MediaDriver$Context.)
              (.dirsDeleteOnStart (-> opts :options :delete-dirs)))]
    (try (MediaDriver/launch ctx)
         (catch IllegalStateException ise
           (throw (Exception. aeron-launch-error-message ise))))))

(defn -main [& args]
  (let [opts (parse-opts args cli-options)]
    (if (-> opts :options :help)
      (do (run! (fn [opt]
                  (println (clojure.string/join " " (take 3 opt))))
                cli-options))
      (do (run-media-driver!)
          (println "Launched the Media Driver. Blocking forever...")
          (<!! (chan))))))
