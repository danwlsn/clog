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
  (into [:div {:class "db dtc-l v-mid w-100 w-75-l tc tr-l"}]
        (map (fn [page]
               [:a {:href (str "/" path page) :class "link dim dark-gray f6 f5-l dib mr3 mr4-l"} (formatter page) ]
               ) page-list)))

(defn build-link-list [page-list path formatter]
  (into [:ul {:class "list pl0"}]
        (map (fn [page]
               [:li {:class "lh-copy pv1"}
                [:a {:href (str "/" path page) :class "link"} (formatter page) ]]
               ) page-list)))

(defn page->path [page path] (str path page ".md"))

(defn remove-date [title]
  (str/replace (nth (str/split title #"_") 1) "-" " "))

(defn posts []
  (vec [:div {:class "w100"} (build-link-list (get-page-list "posts") "posts/" remove-date)]))

(defn layout [content]
  (markup/html
   (page/html5
    [:head
     [:title "Dan Wilson CLOGS"]]
    (page/include-css "https://unpkg.com/tachyons@4.10.0/css/tachyons.min.css")
    [:body {:class "sans-serif"}
     [:div {:class "mw9 center ph3-ns"}
      [:div {:class "cf ph2-ns"}
       [:div {:class "fl w-100 pa2"}
        [:nav {:class "db dt-l w-100 border-box pa3 ph5-l"}
         [:a {:class "db dtc-l v-mid mid-gray link dim w-100 w-25-l tc tl-l mb2 mb0-l"
              :href "/"}
          [:h1 "danwlsn's clog"]
          (build-menu
           (nav '("posts")) "" str/capitalize)]]
        content
        [:footer {:class "ph3 ph4-ns pv4 bt b--black-10 black-70"}
         [:div
          [:a
           {:class "f6 dib pr2 mid-gray dim"
            :href "https://github.com/danwlsn"} "github"]
          [:a
           {:class "f6 dib pr2 mid-gray dim"
            :href "https://twitter.com/danwlsn"} "twitter"]]]]]]])))

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
