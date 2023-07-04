(ns app.system.router
  (:require [integrant.core :as ig]
            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [app.routing.middleware :as mw]))

(defmethod ig/init-key :reitit/router
  [_ {:keys [db]}]
  (println "initializing routes")
  (ring/ring-handler
   (ring/router
    []
    {:data {:env {:db db}
            :muuntaja m/instance
            :middleware mw/global-middleware}})
   (ring/routes
    (ring/redirect-trailing-slash-handler))))
