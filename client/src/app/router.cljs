(ns app.router
  (:require [re-frame.core :as rf]
            [reitit.frontend :as reitit]
            [app.db.patient :as patient.db]
            [app.components.patient-table :refer [patient-table]]
            [app.components.patient-form :refer [patient-form]]))

(def routes
  ["/"
   ["" {:name ::home
        :view patient-table
        :controllers
        [{:start (fn []
                   (rf/dispatch [::patient.db/fetch-patients]))}]}]
   ["patient/:id" {:name ::existing-patient
                   :view patient-form
                   :controllers
                   [{:parameters {:path [:id]}
                     :start (fn [params]
                              (rf/dispatch [::patient.db/fetch-patient
                                            (get-in params [:path :id])]))}]}]
   ["new-patient" {:name ::new-patient
                   :view patient-form
                   :controllers
                   [{:start (fn []
                              (rf/dispatch [::patient.db/reset-form-values]))
                     :stop (fn []
                             (rf/dispatch [::patient.db/reset-form-values]))}]}]])

(def router
  (reitit/router
   routes))
