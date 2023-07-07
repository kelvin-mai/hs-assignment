(ns app.db.ui
  (:require [re-frame.core :as rf]
            [app.db.router :as router.db]))

(def initial-state
  {::ui {:menu-open? false
         :alert {:open false
                 :severity "info"
                 :message "This is an information message!"}}})

(rf/reg-event-db
 ::set-menu
 (fn [db [_ v]]
   (assoc-in db [::ui :menu-open?] v)))

(rf/reg-event-fx
 ::push-state
 (fn [_ [_ link]]
   {:fx [[:dispatch [::set-menu false]]
         [:dispatch [::router.db/push-state link]]]}))

(rf/reg-event-db
 ::set-alert
 (fn [db [_ {:keys [severity
                    message]}]]
   (assoc-in db [::ui :alert] {:open true
                               :severity severity
                               :message message})))

(rf/reg-event-db
 ::close-alert
 (fn [db _]
   (assoc-in db [::ui :alert :open] false)))

(rf/reg-sub
 ::menu-open?
 (fn [db] (get-in db [::ui :menu-open?])))

(rf/reg-sub
 ::alert
 (fn [db] (get-in db [::ui :alert])))
