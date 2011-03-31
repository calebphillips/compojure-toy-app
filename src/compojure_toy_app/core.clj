(ns compojure-toy-app.core
    (:use compojure.core)
    (:use hiccup.core)
    (:use hiccup.page-helpers)
    (:use ring.util.response)
    (:require [compojure.route :as route]
              [compojure.handler :as handler]
              [compojure-toy-app.data :as data]))


(defn format-cd [{:keys [title artist]}]
            [:tr [:td title] [:td artist]])

(defn view-shell [& content]
      (html
        (doctype :xhtml-strict)
        (xhtml-tag "en"
                   [:head
                     [:meta {:http-equiv "Content-type"
                            :content "text/html; charset=utf-8"}]
                     [:title "Play List"]]
                   [:body content])))

(defn cd-list-view []
      (view-shell
        [:h1 "The Play List"]  
        [:table {:border 1}
                [:tr
                  [:th "Title"] [:th "Artist"]]
                (map format-cd (data/select-all))]
        [:p [:a {:href "/cds/new"} "New CD"]]))

(defn new-cd-view []
      (view-shell
        [:h1 "Add a New CD"]
        [:form {:method "post" :action "/cds"}
               [:input {:type "text" :name "title"}]
               [:br]
               [:input {:type "text" :name "artist"}]
               [:br]
               [:input {:type "submit" :value "add"}]]))


(defroutes main-routes
             (GET "/cds" [] (cd-list-view))
             (GET "/cds/new" [] (new-cd-view))
             (POST "/cds" [title artist] 
                   (data/add-cd  (struct data/cd title artist))
                   (redirect "/cds"))
             (route/resources "/")
             (route/not-found "Page not found"))

(def app
     (handler/site main-routes))

