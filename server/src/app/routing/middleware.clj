(ns app.routing.middleware
  (:require [reitit.ring.middleware.muuntaja :as muuntaja]))

(def wrap-env
  {:name ::env
   :compile
   (fn [{:keys [env]} _]
     (fn [handler]
       (fn [request]
         (handler (merge request env)))))})

(def global-middleware
  [muuntaja/format-middleware
   wrap-env])
