(ns app.core
  (:require [app.system.core :refer [read-config]]))

(defn -main []
  (println "application starting")
  (read-config))
