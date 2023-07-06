(ns app.util.router
  (:require [re-frame.core :as rf]
            [reitit.frontend.easy :as rfe]
            [app.db.router :as router.db]))

(defn href
  ([k] (href k nil nil))
  ([k params] (href k params nil))
  ([k params query]
   (rfe/href k params query)))

(defn on-navigate [new-match]
  (when new-match
    (rf/dispatch [::router.db/navigated new-match])))

(defn init-routes! [router]
  (js/console.log "initializing routes")
  (rfe/start!
   router
   on-navigate
   {:use-fragment false}))
