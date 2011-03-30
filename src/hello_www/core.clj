(ns hello-www.core
    (:use compojure.core)
    (:use hiccup.core)
    (:use hiccup.page-helpers)
    (:use ring.util.response)
    (:require [compojure.route :as route]
              [compojure.handler :as handler]))

(defstruct cd :title :artist)

(defn add-records [db & cd] (into db cd))

(defn init-db []
      (add-records #{}
                   (struct cd "My Favorite Things" "John Coltrane")
                   (struct cd "I and Love and You" "The Avett Brothers")
                   (struct cd "The Medicine" "John Mark McMillan")
                   (struct cd "Room for Squares", "John Mayer")))

(def db (ref (init-db)))

(defn cd-rows [db]
      (map (fn [{:keys [title artist]}] 
               [:tr 
                 [:td title]
                 [:td artist]]) db))

(defn view-layout [& content]
      (html
        (doctype :xhtml-strict)
        (xhtml-tag "en"
                   [:head
                     [:meta {:http-equiv "Content-type"
                            :content "text/html; charset=utf-8"}]
                     [:title "Play List"]]
                   [:body content])))

(defn home-page []
      (view-layout
        [:h1 "The Play List"]  
        [:table {:border 1}
                [:tr
                  [:th "Title"] [:th "Artist"]]
                (cd-rows @db)]
        [:p [:a {:href "/cds/new"} "New CD"]]
        ))

(defn new-cd-view []
      (view-layout
        [:h1 "Add a New CD"]
        [:form {:method "post" :action "/cds"}
               [:input {:type "text" :name "title"}]
               [:br]
               [:input {:type "text" :name "artist"}]
               [:br]
               [:input {:type "submit" :value "add"}]]))

(defn add-cd [cd]
      (dosync (alter db conj cd))
      (println (str "added cd " (:title cd))))

(defroutes main-routes
             (GET "/cds" [] (home-page))
             (GET "/cds/new" [] (new-cd-view))
             (POST "/cds" [title artist] 
                   (add-cd (struct cd title artist))
                   (redirect "/cds"))
             (route/resources "/")
             (route/not-found "Page not found"))

(def app
     (handler/site main-routes))

