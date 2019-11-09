(ns clog.handler
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [compojure.core :refer :all]
   [compojure.route :as route]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [hiccup.core :as markup]
   [hiccup.page :as page]
   [markdown-to-hiccup.core :as m]))

(defn get-page-list [dir]
  (map
   (fn [path]
     (str/replace path ".md" ""))
   (.list (io/file (str "_"  dir)))))

(defn nav [extras]
  (concat (get-page-list "pages") extras))

(defn build-menu [page-list path formatter]
  (into [:ul]
        (map (fn [page]
               [:li  [:a {:href (str "/" path page)} (formatter page) ]]
               ) page-list)))

(defn page->path [page path] (str path page ".md"))

(defn remove-date [title]
  (str/replace (nth (str/split title #"_") 1) "-" " "))

(defn posts []
  (vec (list [:h1 {} "posts"]
             (build-menu (get-page-list "posts") "posts/" remove-date))))

(defn layout [content]
  (markup/html
   (page/html5
    [:head
     [:title "Dan Wilson CLOGS"]
     (page/include-css "https://unpkg.com/tachyons@4.10.0/css/tachyons.min.css")]
    [:header "danwlsn clogs"]
    (build-menu
     (nav '("posts")) "" str/capitalize)
    content
    [:footer "follow me on twitter"])))

(defn load-markdown [page dir]
(->> (if (.exists (io/file (page->path page dir)))
       (page->path page dir) (page->path "404" "_errors/"))
     (m/file->hiccup)
     (m/component)))

(defn static [page]
(load-markdown page "_pages/"))

(defn blog [page]
(load-markdown page "_posts/"))

(defn render-page
([render] (layout render)))

(defroutes app-routes
(GET "/" [] (render-page (static "home")))
(GET "/posts" [] (render-page (posts)))
(GET "/posts/:post" [post] (render-page (blog post)))
(GET "/:page" [page] (render-page (static page))))

(def app
(wrap-defaults app-routes site-defaults))
