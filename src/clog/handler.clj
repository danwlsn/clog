(ns clog.handler
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [compojure.core :refer :all]
   [compojure.route :as route]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [hiccup.core :as markup]
   [markdown-to-hiccup.core :as m]))

(defn get-page-list []
  (map
   (fn [path]
     (str/replace path ".md" ""))
   (.list (io/file "_pages"))))

(defn build-menu [page-list]
  (map (fn [page]
         [:a {:href (str "/" page)} page ]) page-list))

(defn page->path [page] (str "_pages/" page ".md"))

(defn layout [page]
  (markup/html [:header "danwlsn clogs"]
               (build-menu (get-page-list))
               (m/file->hiccup
                (if (.exists (io/file page))
                  page (page->path "404")))
               [:footer "follow me on twitter"]))

(defn render-page
  ([page] (layout (page->path page))))

(defroutes app-routes
  (GET "/" [] (render-page "index"))
  (GET "/:page" [page] (render-page page)))

(def app
(wrap-defaults app-routes site-defaults))
