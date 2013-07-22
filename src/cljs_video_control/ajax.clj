(ns cljs-video-control.ajax
  "Methods for AJAX interaction."
  (:require [ring.util [response :as resp]]
            [clj-http.client :as client]
            (cljs-video-control [manifest :as m]
                                [layout :as lx]))
  (:import (java.io File)))

(defn get-store
  "Get clip selection from the store."
  []
  (letfn [(make-item [img]
            {:title (str "F" (rand-int 100))
             :image img
             :colour (repeatedly 3 #(rand-int 256))})]
    (resp/response (map make-item ["P1030301" "P1030382" "P1030388" "P1030043"
                                   "P1030419" "P1030422" "P1030434" "P1030541"
                                   "P1030581" "P1030610" "P1030643" "P1030681"]))))

(defn- read-shot-times
  "Map from shot/slug number to start time, from config file. (We don't actually
   need this: it's in the directory names!)"
  []
  ;; Rather ugly: look in parent of file root.
  (let [shot-file (File. (:shots-file-root m/CONFIG)
                         "shotList.txt")]
    (reduce
     (fn [r line] (let [[clip ts] (next (re-find #"(\d+)\s+(\d+)" line))]
                   (assoc r (Integer/parseInt clip) (Integer/parseInt ts))))
     { }
     (line-seq
      (clojure.java.io/reader shot-file)))))

(defn show-time [frame]
  (let [secs (int (/ frame 25))
        ss (mod secs 60)
        mm (mod (int (/ secs 60)) 60)
        hh (int (/ secs 3600))]
    (format "%01d:%02d:%02d" hh mm ss)))

(defn get-clips
  "Get clip list (eventually this'll be scrollable by 'bank')."
  []
  (let [shot-times (read-shot-times)]
    (letfn [(make-item [[shot frame-lo frame-hi]]
              (let [a (lx/assets shot frame-lo frame-hi)]
                (assoc a
                  :slug shot
                  :timestamp (show-time (Integer/parseInt frame-lo)))))]

      (resp/response (sort-by :slug (map (comp make-item
                                               next
                                               (partial re-find #"(\d+)_(\d+)_(\d+)*")
                                               str)
                                         (seq (.listFiles (File. (:shots-file-root m/CONFIG)
                                                                 "shots")))))))))

(defn post-active
  "Post an item for the active sequence."
  [p]
  (println "PARAMS: " p)
  (resp/response {:id 999
                  :title "SAVED"}))

(defn upload
  [p]
  (if (:do-upload m/CONFIG)
    (let [s (lx/field "upload")
          r (client/post s {:form-params p})]
      (println (format ">> POST [%s: %s -> %s]" s p r))
      r)
    (resp/response { })))
