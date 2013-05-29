(ns cljs-video-control.core
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [hiccup.page :as hp]
            [hiccup.util :as hu]))

(defn render-body []
  (hp/html5
   [:head
    (hp/include-js
       "http://code.jquery.com/jquery-1.8.2.min.js"
       "http://underscorejs.org/underscore.js"
       "http://backbonejs.org/backbone.js")]
    [:body
     [:button#clickable-event "Click to trigger an alert from basic Backbone event"]
     [:button#clickable-color "Click to change background color"]
     (hp/include-js "js/demo.js")]))

(defn render-video []
  (hp/html5
   [:head
    (hp/include-js
       "http://code.jquery.com/jquery-1.8.2.min.js"
       "http://underscorejs.org/underscore.js"
       "http://backbonejs.org/backbone.js")
    (hp/include-css "css/style.css")]
   [:body
    [:script#search_template {:type "text/template"}
     [:label "<%= search_label %>"]
     [:input#search_input {:type "text"}]
     [:input#search_button {:type "button" :value "Search"}]]

    [:div
     [:video#video
      {:controls 1
       :preload "none"
       :poster "http://media.w3.org/2010/05/sintel/poster.png"}
      [:source#mp4
       {:src "http://media.w3.org/2010/05/sintel/trailer.mp4"
        :type "video/mp4"}]]]

    [:div#buttons
     [:button {:onclick "document._video.load()"} "load()"]
     [:button {:onclick "document._video.play()"} "play()"]
     [:button {:onclick "document._video.pause()"} "pause()"]
     [:button {:onclick "document._video.currentTime+=10"} "currentTime+=10"]
     [:button {:onclick "document._video.currentTime-=10"} "currentTime-=10"]
     [:button {:onclick "document._video.currentTime=50"} "currentTime=50"]
     [:button {:onclick "document._video.playbackRate++"} "playbackRate++"]
     [:button {:onclick "document._video.playbackRate--"} "playbackRate--"]
     [:button {:onclick "document._video.playbackRate+=0.1"} "playbackRate+=0.1"]
     [:button {:onclick "document._video.playbackRate-=0.1"} "playbackRate-=0.1"]

     [:button#bash "BASH"]]

    [:div [:p#status "init"]]

    [:div#search_container]

    (hp/include-js "js/video.js")]))

(defroutes my-routes
  (GET "/" [] (render-body))
  (GET "/video" [] (render-video))
  (route/resources "/" {:root "public"}))

(def app
  (handler/site my-routes))
