(ns app.db.ui
  (:require [re-frame.core :as rf]
            [app.db.router :as router.db]))

(def initial-state
  {::ui {:menu-open? false}})

(rf/reg-event-db
 ::set-menu
 (fn [db [_ v]]
   (assoc-in db [::ui :menu-open?] v)))

(rf/reg-event-fx
 ::push-state
 (fn [_ [_ link]]
   {:fx [[:dispatch [::set-menu false]]
         [:dispatch [::router.db/push-state link]]]}))

(rf/reg-sub
 ::menu-open?
 (fn [db] (get-in db [::ui :menu-open?])))
