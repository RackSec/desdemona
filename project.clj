(defproject desdemona "0.1.0-SNAPSHOT"
  :description "Data-backed security operations"
  :url "https://github.com/racksec/desdemona"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]

                 [org.onyxplatform/onyx "0.8.8"]
                 [org.onyxplatform/onyx-sql "0.8.8.0"]
                 [org.onyxplatform/onyx-kafka "0.8.8.0"]
                 [org.onyxplatform/onyx-seq "0.8.8.0"]

                 [cheshire "5.5.0"]
                 [aero "0.2.0"]
                 [org.clojure/tools.cli "0.3.3"]
                 [mysql/mysql-connector-java "5.1.38"]

                 [byte-streams "0.2.1"]
                 [camel-snake-kebab "0.3.2"]

                 [org.clojure/clojurescript "1.8.40"]
                 [reagent "0.6.0-alpha"]
                 [reagent-forms "0.5.22"]
                 [reagent-utils "0.1.7"]
                 [hiccup "1.0.5"]
                 [secretary "1.2.3"]
                 [venantius/accountant "0.1.7"]
                 [com.cemerick/piggieback "0.2.2-SNAPSHOT"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [cljsjs/react-bootstrap "0.28.1-1"
                  :exclusions [org.webjars.bower/jquery]]
                 [ring/ring-defaults "0.2.0"]
                 [ring/ring-mock "0.3.0"]

                 [org.clojure/core.logic "0.8.10"]
                 [org.clojure/core.match "0.3.0-alpha4"]

                 [instaparse "1.4.1"]

                 [com.gfredericks/system-slash-exit "0.2.0"]]
  :plugins [[lein-cljfmt "0.3.0"]
            [lein-cloverage "1.0.7-SNAPSHOT"]
            [lein-kibit "0.1.2"]
            [jonase/eastwood "0.2.3"]
            [lein-cljsbuild "1.1.2"]
            [lein-figwheel "0.5.0-1"]
            [lein-npm "0.6.2"]
            [lein-doo "0.1.6"]
            [lein-scss "0.2.3"]
            [lein-pdo "0.1.1"]]
  :aliases {"figsass" ["pdo" ["scss" ":dev" "auto"] ["figwheel"]]}
  :npm {:dependencies [[karma ""]
                       [karma-cljs-test ""]
                       [karma-firefox-launcher ""]]}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["env/dev/desdemona/ui/dev.cljs" "src"]
                        :figwheel true
                        :compiler {:main "desdemona.dev"
                                   :output-to "resources/ui/js/main.js"
                                   :output-dir "resources/ui/js/out"
                                   :asset-path "js/out"}}
                       {:id "test"
                        :source-paths ["src" "test"]
                        :compiler {:main "desdemona.ui.runner"
                                   :output-to "target/cljs-tests/test.js"
                                   :optimizations :none}}]}
  :scss {:builds
         {:dev {:source-dir "resources/ui/sass/"
                :dest-dir "resources/ui/css/"
                :executable "sassc"
                :args ["-m" "-I" "resources/ui/sass/" "-t" "nested"]}}}
  :doo {:paths {:karma "node_modules/karma/bin/karma"}}
  :figwheel {:http-server-root "ui"
             :css-dirs ["resources/ui/css"]
             :server-port 3449
             :nrepl-port 7002
             :nrepl-middleware ["cider.nrepl/cider-middleware"
                                "refactor-nrepl.middleware/wrap-refactor"
                                "cemerick.piggieback/wrap-cljs-repl"]
             :ring-handler desdemona.ui.server/server}
  :cljfmt {:indents {run [[:inner 0]] ;; core.logic
                     fresh [[:inner 0]]}} ;; core.logic
  :auto {:default {:file-pattern #"\.(clj|cljs|cljx|edn|ebnf)$"}}
  :profiles {:uberjar {:aot [desdemona.launcher.aeron-media-driver
                             desdemona.launcher.launch-prod-peers]}
             :dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]]
                   :source-paths ["env/dev" "src"]}})
