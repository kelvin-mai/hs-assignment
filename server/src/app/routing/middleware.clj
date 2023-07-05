(ns app.routing.middleware
  (:require [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.coercion :as coercion]
            [app.routing.exception :as exception]))

(def wrap-env
  {:name ::env
   :compile
   (fn [{:keys [env]} _]
     (fn [handler]
       (fn [request]
         (handler (merge request env)))))})

(def global-middleware
  [muuntaja/format-middleware
   exception/middleware
   coercion/coerce-request-middleware
   coercion/coerce-response-middleware
   wrap-env])
