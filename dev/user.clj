(ns user
  (:require [clojure.tools.namespace.repl :as tools-ns]
            [integrant.repl :as ig-repl :refer [go halt]]
            [integrant.repl.state :as state]
            [nrepl.server]
            [app.system.core :refer [read-config]]))

(tools-ns/set-refresh-dirs "dev" "server/src")

(ig-repl/set-prep!
 (fn []
   (read-config :dev)))

(declare router db)

(defn start-interactive []
  (go)
  :ready!)

(defn restart []
  (halt)
  (tools-ns/refresh :after 'user/start-interactive))

(comment
  (restart)
  (halt))
