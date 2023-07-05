(ns app.api.routes
  (:require [app.api.patient.handler :as patient]))

(def health-route
  ["/health-check"
   {:name ::health-check
    :get
    (fn [_]
      {:status 200
       :body {:ping "pong"}})}])

(def api-routes
  ["/api"
   health-route
   patient/routes])
