(ns clog.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hiccup.core :as markup]))

(def name "Danny Wilson")
(defroutes app-routes
  (GET "/" [] (markup/html [:h1 "Wilson"]))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
