(ns compojure-toy-app.core
    (:use compojure.core)
    (:use hiccup.core)
    (:use hiccup.page-helpers)
    (:use ring.util.response)
    (:require [compojure.route :as route]
              [compojure.handler :as handler]
              [compojure-toy-app.data :as data]))


(defn format-cd [{:keys [title artist]}]
            [:tr [:td title] [:td artist] 
                 [:td [:form {:method "post" :action "/cds/delete"}
                             [:input {:type "hidden" :name "title" :value title}]
                             [:input {:type "hidden" :name "artist" :value artist}]
                             [:input {:type "submit" :value "X"}]
                             ]]])


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
                  [:th "Title"] [:th "Artist"] [:th "&nbsp;"]]
                (map format-cd (data/select-all))]
        [:p [:a {:href "/cds/new"} "New CD"]]))

(defn new-cd-view [& [error-msg title artist]]
      (view-shell
        [:h1 "Add a New CD"]
        (if error-msg
          [:p error-msg])
        [:form {:method "post" :action "/cds"}
               [:span "Title:"] [:input {:type "text" :name "title" :value title}]
               [:br]
               [:span "Artist:"][:input {:type "text" :name "artist" :value artist}]
               [:br]
               [:input {:type "submit" :value "add"}]]))

(defn handle-post [title artist]
      (if (or (empty? title) (empty? artist)) 
        (new-cd-view "Required Info Missing" title artist)
        (do 
          (data/add-cd  (struct data/cd title artist))
          (redirect "/cds"))))

(defn handle-delete [title artist]
      (data/remove-cd  (struct data/cd title artist))
      (redirect "/cds"))

(defroutes main-routes
             (GET "/cds" [] (cd-list-view))
             (GET "/cds/new" [] (new-cd-view))
             (POST "/cds" [title artist] (handle-post title artist))
             (POST "/cds/delete" [title artist] (handle-delete title artist))
             (route/resources "/")
             (route/not-found "Page not found"))

(def app
     (handler/site main-routes))

