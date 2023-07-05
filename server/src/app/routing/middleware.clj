(ns app.routing.middleware
  (:require [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.coercion :as coercion]
            [ring.middleware.cors :refer [wrap-cors]]
            [app.routing.exception :as exception]))

(def wrap-env
  {:name ::env
   :compile
   (fn [{:keys [env]} _]
     (fn [handler]
       (fn [request]
         (handler (merge request env)))))})

(def global-middleware
  [parameters/parameters-middleware
   muuntaja/format-middleware
   [wrap-cors
    :access-control-allow-origin [#"http://localhost:3000"]
    :access-control-allow-methods [:get :post :put :delete]]
   exception/middleware
   coercion/coerce-request-middleware
   coercion/coerce-response-middleware
   wrap-env])
