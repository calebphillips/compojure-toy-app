(ns compojure-toy-app.core
    (:use compojure.core)
    (:use hiccup.core)
    (:use hiccup.page-helpers)
    (:use ring.util.response)
    (:use compojure-toy-app.data)
    (:require [compojure.route :as route]
              [compojure.handler :as handler]))


(def db (ref (compojure-toy-app.data/init-db)))

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

