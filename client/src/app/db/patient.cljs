(ns app.db.patient
  (:require [re-frame.core :as rf]
            [app.util.conversion :refer [stringify-kw]]))

(def initial-state
  {::patient {:pagination {:page 0
                           :rows-per-page 10
                           :attr :patient/created
                           :dir :desc}
              :data []}})

(rf/reg-event-fx
 ::fetch-patients
 (fn [{:keys [db]} [_]]
   (let [{:keys [page
                 rows-per-page
                 attr
                 dir]} (get-in db [::patient :pagination])
         offset (* page rows-per-page)
         url "/api/patient"]
     {:fx [[:dispatch [:http {:url url
                              :query {:limit rows-per-page
                                      :offset offset
                                      :attr (stringify-kw attr)
                                      :dir dir}
                              :method :get
                              :on-success [::fetch-patients-success]}]]]})))

(rf/reg-event-db
 ::fetch-patients-success
 (fn [db [_ {:keys [data]}]]
   (assoc-in db [::patient :data] data)))

(rf/reg-event-fx
 ::set-page
 (fn [{:keys [db]} [_ n]]
   {:db (assoc-in db [::patient :pagination :page] n)
    :fx [[:dispatch [::fetch-patients]]]}))

(rf/reg-event-fx
 ::set-rows-per-page
 (fn [{:keys [db]} [_ n]]
   {:db (assoc-in db [::patient :pagination :rows-per-page] n)
    :fx [[:dispatch [::fetch-patients]]]}))

(rf/reg-event-fx
 ::set-sort
 (fn [{:keys [db]} [_ kw]]
   (let [{:keys [attr dir]} (get-in db [::patient :pagination])
         {:keys [attr dir]} (cond (not= attr kw) {:attr kw
                                                  :dir :asc}
                                  (= :asc dir) {:attr attr :dir :desc}
                                  (or (= :desc dir) (nil? dir)) {:attr attr :dir :asc})]
     {:db (-> db
              (assoc-in [::patient :pagination :attr] attr)
              (assoc-in [::patient :pagination :dir] dir))
      :fx [[:dispatch [::fetch-patients]]]})))

(rf/reg-sub
 ::patients
 (fn [db] (get-in db [::patient :data])))

(rf/reg-sub
 ::pagination
 (fn [db] (get-in db [::patient :pagination])))
