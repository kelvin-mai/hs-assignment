(ns app.system.server
  (:require [integrant.core :as ig]
            [org.httpkit.server :as http]))

(defmethod ig/init-key :http/server
  [_ {:keys [router config]}]
  (let [port (:port config)]
    (println "server starting on port" port)
    (http/run-server router {:port port})))

(defmethod ig/halt-key! :http/server
  [_ server]
  (println "server stopping")
  (server :timeout 100))
