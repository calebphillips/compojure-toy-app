(defproject compojure-toy-app "1.0.0-SNAPSHOT"
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.6.2"]
                 [hiccup "0.3.4"]]
  :dev-dependencies [[lein-ring "0.4.0"]]
  :ring {:handler compojure-toy-app.core/app})
