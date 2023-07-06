(ns app.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [app.components.core :refer [app]]
            [app.router :refer [router]]
            [app.util.router :refer [init-routes!]]
            app.db.core))

(defn ^:dev/after-load reload []
  (rf/clear-subscription-cache!)
  (init-routes! router)
  (rdom/render [app]
               (.getElementById js/document "app"))
  (js/console.log "reloaded"))

(defn ^:export init []
  (js/console.log "application starting")
  (rf/dispatch-sync [:initialize-db])
  (js/console.log "begin rendering")
  (reload))
