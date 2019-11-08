(ns clog.handler
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [compojure.core :refer :all]
   [compojure.route :as route]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [hiccup.core :as markup]
   [markdown-to-hiccup.core :as m]))

(defn get-page-list [dir]
  (map
   (fn [path]
     (str/replace path ".md" ""))
   (.list (io/file dir))))

(defn build-menu [page-list path]
  (map (fn [page]
         [:a {:href (str "/" path page)} page ]) page-list))

(defn page->path [page path] (str path page ".md"))

(defn posts []
  (build-menu (get-page-list "_posts") "posts/"))

(defn layout [content]
  (markup/html [:header "danwlsn clogs"]
               (build-menu (get-page-list "_pages") "/")
               (content)
               [:footer "follow me on twitter"]))

(defn static [page]
  (m/file->hiccup
   (if (.exists (io/file (page->path page "_pages/")))
     (page->path page "_pages/") (page->path "404" "_pages/"))))

(defn render-page
  ([render] (layout render)))

(defroutes app-routes
  (GET "/" [] (render-page (static "index")))
  (GET "/posts" [] (render-page posts))
  (GET "/posts/:post" [post] (render-page (static "index")))
  (GET "/:page" [page] (render-page (static "index"))))

(def app
  (wrap-defaults app-routes site-defaults))
