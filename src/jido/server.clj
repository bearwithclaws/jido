(ns jido.server
 (:use compojure.core
        [clojure.tools.logging :only [info debug warn error]]
        ;; twitter
        [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.restful]
        ;; for view
        [hiccup.core :only [html]]
        [hiccup.form :only (form-to label text-area submit-button)]
        [hiccup.page :only [html5 include-css include-js]])
  (:require [org.httpkit.server :as server]
            [clojure.tools.nrepl.server :as nrepl]
            [cemerick.shoreleave.rpc :refer (defremote) :as rpc]
            [clojure.string :as str]
            [clj-http.client :as client]
            [ring.middleware.reload :as reload]
            [ring.util.response :as ring]
            [compojure.handler :as handler]
            [compojure.route :as route])
  (:import
   (twitter.callbacks.protocols SyncSingleCallback)))

;; state
(defonce prod? (atom (System/getenv "LEIN_NO_DEV")))
(defonce counter (atom 0))

;; templates
(defn index [& [links]]
  (html
    [:head
     [:title "Jido"]
     (include-css "//netdna.bootstrapcdn.com/twitter-bootstrap/2.2.1/css/bootstrap-combined.min.css"
                  "/css/styles.css")]
    [:body
     [:div.container
      [:h1 "Jido"]
      [:h4 "Intelligent Twitter Link Auto-Posting Tool"]
      (form-to [:post "/"]
        (label "tweeter" "Enter Twitter username")
        (text-area "tweeter")
        [:br]
        (submit-button {:class "btn"} "Get Links!"))
      [:hr]
      [:ul (for [link links] (when link [:li (str "<a href='" link "'>" link "</a>")]))]]
     (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js")
     (include-js "//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/js/bootstrap.min.js")
     ]))

;; handler
(def my-creds (make-oauth-creds (System/getenv "TWITTER_APP_SECRET")
                                (System/getenv "TWITTER_APP_KEY")
                                (System/getenv "TWITTER_CONSUMER_SECRET")
                                (System/getenv "TWITTER_CONSUMER_KEY")))

(defn get-tweets [tweeter]
  (:body (statuses-user-timeline
             :oauth-creds my-creds
             :params {:screen-name tweeter})))

(defn get-links [tweeter]
  (vec (map :expanded_url (map first (map :urls (map :entities (get-tweets tweeter)))))))

(defn list-links [tweeter]
  (when-not (str/blank? tweeter)
   (def links (get-links [tweeter])))
  (index links))

; routes
(defroutes app-routes
  (GET "/" [] (index))
  (POST "/" [tweeter] (list-links tweeter))
  (route/resources "/")
  (route/not-found "Not Found"))

(def all-routes (rpc/wrap-rpc app-routes))

(def app
  (if @prod?
    (handler/site all-routes)
    (reload/wrap-reload (handler/site all-routes))))

;; init
(defn start-nrepl-server [port]
  (info "Starting nrepl server on port" port)
  (defonce server (nrepl/start-server :port port)))

(defn start-app [port]
  (info "Starting server on port" port)
  (server/run-server app {:port port :join? false}))

(defn -main [& args]
  (when-not @prod? (start-nrepl-server 7888))
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (start-app port)))