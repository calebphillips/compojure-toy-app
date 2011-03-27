(ns hello-www.core
    (:use compojure.core)
    (:use hiccup.core)
    (:use hiccup.page-helpers)
    (:require [compojure.route :as route]
              [compojure.handler :as handler]))

(defstruct cd :title :artist)

(defn add-records [db & cd] (into db cd))

(defn init-db []
      (add-records #{}
                   (struct cd "My Favorite Things" "John Coltrane")
                   (struct cd "I and Love and You" "The Avett Brothers")
                   (struct cd "The Medicine" "John Mark McMillan")))

(defn db-to-html [db]
      (map (fn [cd] [:tr 
                      [:td (:title cd)]
                      [:td (:artist cd)]]) db))

(defn home-page []
      (html
        (doctype :xhtml-strict)
        (xhtml-tag "en"
                   [:body [:h1 "The Home Page"]  
                          [:table {:border 1}
                            [:tr
                              [:th "Title"] [:th "Artist"]]
                            (db-to-html (init-db))]])))
(defroutes main-routes
             (GET "/" [] (home-page))
             (GET "/user/:id" [id] (str "I sense that you are user " id))
             (route/resources "/")
             (route/not-found "Page not found"))

(def app
     (handler/site main-routes))

