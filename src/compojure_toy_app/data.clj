(ns compojure-toy-app.data)

(defstruct cd :title :artist)

(defn add-records [db & cd] (into db cd))

(defn init-db []
      (add-records (sorted-set-by #(compare (%1 :title) (%2 :title)))
                   (struct cd "My Favorite Things" "John Coltrane")
                   (struct cd "I and Love and You" "The Avett Brothers")
                   (struct cd "The Medicine" "John Mark McMillan")
                   (struct cd "Room for Squares", "John Mayer"))) 
