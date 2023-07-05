(ns app.system.router
  (:require [integrant.core :as ig]
            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.malli :as malli]
            [app.routing.middleware :as mw]
            [app.routing.coercion :refer [coercion]]
            [app.api.routes :refer [api-routes]]))

(defmethod ig/init-key :reitit/router
  [_ {:keys [db]}]
  (println "initializing routes")
  (ring/ring-handler
   (ring/router
    [api-routes]
    {:data {:env {:db db}
            :coercion coercion
            :muuntaja m/instance
            :middleware mw/global-middleware}})
   (ring/routes
    (ring/redirect-trailing-slash-handler))))
