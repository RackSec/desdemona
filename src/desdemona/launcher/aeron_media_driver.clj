(ns desdemona.launcher.aeron-media-driver
  (:gen-class)
  (:require [clojure.core.async :refer [chan <!!]]
            [clojure.tools.cli :refer [parse-opts]])
  (:import [uk.co.real_logic.aeron Aeron$Context]
           [uk.co.real_logic.aeron.driver MediaDriver MediaDriver$Context]))

(def cli-options
  [["-d" "--delete-dirs"
    "Delete the media drivers directory on startup"
    :default false]
   ["-h" "--help"
    "Display a help message"]])

(defn -main [& args]
  (let [opts (parse-opts args cli-options)
        {:keys [help delete-dirs]} (:options opts)
        ctx (cond-> (MediaDriver$Context.)
              delete-dirs (.dirsDeleteOnStart delete-dirs))
        media-driver (try (MediaDriver/launch ctx)
                          (catch IllegalStateException ise
                            (throw (Exception. "Error starting media driver. This may be due to a media driver data incompatibility between versions. Check that no other media driver has been started and then use -d to delete the directory on startup" ise))))]
    (when help
      (run! (fn [opt]
              (println (clojure.string/join " " (take 3 opt))))
            cli-options)
      (System/exit 0))
    (println "Launched the Media Driver. Blocking forever...")
    (<!! (chan))))
