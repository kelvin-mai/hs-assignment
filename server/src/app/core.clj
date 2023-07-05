(ns app.core
  (:require [integrant.core :as ig]
            [app.system.core :refer [read-config]]))

(defn -main []
  (-> (read-config)
      (ig/init)))
