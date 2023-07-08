(ns app.db.patient
  (:require [re-frame.core :as rf]
            [app.util.conversion :refer [stringify-kw
                                         remove-nils
                                         dayjs->date-string
                                         inst-string->dayjs]]))

(def initial-form
  {:patient/name nil
   :patient/address nil
   :patient/sex nil
   :patient/gender nil})

(def initial-state
  {::patient {:pagination {:page 0
                           :rows-per-page 10
                           :attr :patient/created
                           :dir :desc}
              :editable? false
              :show-validation? false
              :form initial-form
              :filters initial-form
              :data []}})

(rf/reg-event-fx
 ::search-patients
 (fn [{:keys [db]} _]
   {:db (assoc-in db [::patient :pagination :page] 0)
    :fx [[:dispatch [::fetch-patients]]]}))

(rf/reg-event-fx
 ::fetch-patients
 (fn [{:keys [db]} [_]]
   (let [{:keys [page
                 rows-per-page
                 attr
                 dir]} (get-in db [::patient :pagination])
         offset (* page rows-per-page)
         filters (-> db
                     (get-in [::patient :filters])
                     (remove-nils))
         query (merge {:limit rows-per-page
                       :offset offset
                       :attr (stringify-kw attr)
                       :dir dir}
                      filters)
         url "/api/patient"]
     {:fx [[:dispatch [:http {:url url
                              :query query
                              :method :get
                              :on-success [::fetch-patients-success]}]]]})))

(rf/reg-event-db
 ::fetch-patients-success
 (fn [db [_ {:keys [data]}]]
   (assoc-in db [::patient :data] data)))

(rf/reg-event-fx
 ::fetch-patient
 (fn [_ [_ id]]
   {:fx [[:dispatch [:http {:url (str "/api/patient/" id)
                            :method :get
                            :on-success [::fetch-patient-success]}]]]}))

(rf/reg-event-db
 ::fetch-patient-success
 (fn [db [_ {:keys [data]}]]
   (let [data (-> data
                  (assoc :patient/dob (inst-string->dayjs (:patient/dob data))))]
     (assoc-in db [::patient :form] data))))

(rf/reg-event-fx
 ::validate-submit-patient-form
 (fn [{:keys [db]} _]
   (let [data (get-in db [::patient :form])
         valid? (and (:patient/name data)
                     (:patient/sex data)
                     (:patient/dob data))]
     {:db (if valid?
            db
            (assoc-in db [::patient :show-validation?] true))
      :fx [(when valid? [:dispatch [::submit-patient-form]])]})))

(rf/reg-event-fx
 ::submit-patient-form
 (fn [{:keys [db]} [_]]
   (let [data (get-in db [::patient :form])
         current-route (get-in db [:app.db.router/router :data :name])
         id (get-in db [:app.db.router/router :parameters :path :id])
         data (cond-> data
                true (assoc :patient/dob (dayjs->date-string (:patient/dob data)))
                true (remove-nils)
                (= current-route :app.router/new-patient) (update-keys (comp keyword name))
                (= current-route :app.router/existing-patient) (dissoc :patient/created))
         url "/api/patient"
         url (if (= current-route :app.router/new-patient)
               url
               (str url "/" id))
         method (if (= current-route :app.router/new-patient)
                  :post
                  :put)]
     {:db db
      :fx [[:dispatch [:http {:url url
                              :data data
                              :method method
                              :on-success [::submit-patient-form-success]}]]]})))

(rf/reg-event-fx
 ::submit-patient-form-success
 (fn [_ _]
   {:fx [[:dispatch [:app.db.ui/set-alert {:severity "success"
                                           :message "Patient data successfully submitted."}]]
         [:dispatch [:app.db.router/push-state :app.router/home]]]}))

(rf/reg-event-fx
 ::delete-patient
 (fn [_ [_ id]]
   {:fx [[:dispatch [:http {:url (str "/api/patient/" id)
                            :method :delete
                            :on-success [::delete-patient-success]}]]]}))

(rf/reg-event-fx
 ::delete-patient-success
 (fn [_ _]
   {:fx [[:dispatch [:app.db.ui/set-alert {:severity "success"
                                           :message "Patient data successfully deleted."}]]
         [:dispatch [:app.db.router/push-state :app.router/home]]]}))

(rf/reg-event-db
 ::set-editable?
 (fn [db [_ v]]
   (assoc-in db [::patient :editable?] v)))

(rf/reg-event-fx
 ::set-page
 (fn [{:keys [db]} [_ n]]
   {:db (assoc-in db [::patient :pagination :page] n)
    :fx [[:dispatch [::fetch-patients]]]}))

(rf/reg-event-fx
 ::set-rows-per-page
 (fn [{:keys [db]} [_ n]]
   {:db (-> db
            (assoc-in [::patient :pagination :page] 0)
            (assoc-in [::patient :pagination :rows-per-page] n))
    :fx [[:dispatch [::fetch-patients]]]}))

(rf/reg-event-fx
 ::set-sort
 (fn [{:keys [db]} [_ kw]]
   (let [{:keys [rows-per-page attr dir]} (get-in db [::patient :pagination])
         {:keys [attr dir]} (cond (not= attr kw) {:attr kw
                                                  :dir :asc}
                                  (= :asc dir) {:attr attr :dir :desc}
                                  (or (= :desc dir) (nil? dir)) {:attr attr :dir :asc})]
     {:db (assoc-in db [::patient :pagination]
                    {:page 0
                     :rows-per-page rows-per-page
                     :attr attr
                     :dir dir})
      :fx [[:dispatch [::fetch-patients]]]})))

(rf/reg-event-db
 ::set-form-value
 (fn [db [_ attr v]]
   (assoc-in db [::patient :form attr] v)))

(rf/reg-event-db
 ::reset-form-values
 (fn [db [_ _]]
   (-> db
       (assoc-in [::patient :form] initial-form)
       (assoc-in [::patient :show-validation?] false))))

(rf/reg-event-db
 ::set-filter-value
 (fn [db [_ attr v]]
   (assoc-in db [::patient :filters attr] v)))

(rf/reg-event-db
 ::reset-filter-values
 (fn [db [_ _]]
   (assoc-in db [::patient :filters] initial-form)))

(rf/reg-sub
 ::patients
 (fn [db] (get-in db [::patient :data])))

(rf/reg-sub
 ::pagination
 (fn [db] (get-in db [::patient :pagination])))

(rf/reg-sub
 ::editable?
 (fn [db] (get-in db [::patient :editable?])))

(rf/reg-sub
 ::form
 (fn [db] (get-in db [::patient :form])))

(rf/reg-sub
 ::filters
 (fn [db] (get-in db [::patient :filters])))

(rf/reg-sub
 ::show-validation?
 (fn [db] (get-in db [::patient :show-validation?])))
