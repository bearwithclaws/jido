(defproject jido "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [http-kit "2.1.3"]
                 [org.clojure/tools.logging "0.2.6"]
                 [hiccup "1.0.3"]
                 [ring/ring-devel "1.2.0-beta3"]
                 [ring/ring-core "1.2.0-beta3"]
                 [compojure "1.1.5"]
                 [clj-http "0.7.2"]
                 [twitter-api "0.7.4"]
                 [clojurewerkz/urly "1.0.0"]   
                 [com.taoensso/carmine "1.12.0"]
                 [shoreleave/shoreleave-remote "0.3.0"]
                 [com.cemerick/shoreleave-remote-ring "0.0.2"]
                 [jayq "2.0.0"]
                 [org.clojure/tools.nrepl "0.2.3"]]
  :plugins [[lein-ring "0.8.5"]
            [lein-cljsbuild "0.3.2"]]
  :main jido.server
  :min-lein-version "2.0.0"

  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]]}})