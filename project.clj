(defproject desdemona "0.1.0-SNAPSHOT"
  :description "Data-backed security operations"
  :url "https://github.com/racksec/desdemona"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.onyxplatform/onyx "0.8.2"]
                 [org.clojure/core.logic "0.8.10"]]
  :plugins [[lein-cljfmt "0.3.0"]
            [lein-cloverage "1.0.7-SNAPSHOT"]]
  :cljfmt {:indents {run [[:inner 0]] ;; core.logic
                     fresh [[:inner 0]]}} ;; core.logic
  :profiles {:uberjar {:aot [desdemona.launcher.aeron-media-driver
                             desdemona.launcher.launch-prod-peers]}
             :dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]]
                   :source-paths ["env/dev" "src"]}})
