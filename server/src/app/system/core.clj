(ns app.system.core
  (:require [aero.core :as aero]
            [integrant.core :as ig]
            app.system.db
            app.system.router
            app.system.server))

(defmethod aero/reader 'ig/ref
  [_ _ value]
  (ig/ref value))

(defmethod ig/init-key :system/config
  [_ config]
  (println "system starting with config " config)
  config)

(defn read-config
  ([] (read-config :prod))
  ([profile]
   (aero/read-config "resources/config.edn" {:profile profile})))
