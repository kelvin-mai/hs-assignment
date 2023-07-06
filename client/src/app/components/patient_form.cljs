(ns app.components.patient-form
  (:require [re-frame.core :as rf]
            [clojure.string :as s]
            [app.db.router :as router.db]
            [app.db.patient :as patient.db]
            ["@mui/material" :refer [Button
                                     FormControl
                                     InputLabel
                                     MenuItem
                                     Grid
                                     Select
                                     TextField
                                     Typography]]
            ["@mui/x-date-pickers/AdapterDayjs" :refer [AdapterDayjs]]
            ["@mui/x-date-pickers" :refer [LocalizationProvider
                                           DatePicker
                                           DateCalendar]]))

(defn input
  [{:keys [xs label attr value required disabled]}]
  [:> Grid {:item true
            :xs xs}
   [:> TextField {:full-width true
                  :required required
                  :disabled disabled
                  :label label
                  :value (or value "")
                  :on-change #(rf/dispatch [::patient.db/set-form-value attr
                                            (.. % -target -value)])}]])

(defn select-options [value]
  ^{:key value}
  [:> MenuItem {:value value} (s/capitalize value)])

(defn select
  [{:keys [xs label attr value options required disabled]}]
  [:> Grid {:item true
            :xs xs}
   [:> FormControl {:full-width true
                    :required required}
    [:> InputLabel label]
    [:> Select {:label label
                :value (or value "")
                :disabled disabled
                :on-change #(rf/dispatch [::patient.db/set-form-value attr
                                          (.. % -target -value)])}
     [:> MenuItem {:value nil} "Unselect"]
     (map select-options options)]]])

(defn datepicker [{:keys [xs label attr value required disabled]}]
  [:> Grid {:item true
            :xs xs}
   [:> FormControl {:full-width true
                    :required required}
    [:> InputLabel label]
    [:> LocalizationProvider {:dateAdapter AdapterDayjs}
     [:> DateCalendar {:label label
                       :value value
                       :disabled disabled
                       :on-change #(rf/dispatch [::patient.db/set-form-value attr %])}]]]])

(defn patient-form []
  (let [route @(rf/subscribe [::router.db/current-route])
        current-route (get-in route [:data :name])
        existing-patient (= current-route :app.router/existing-patient)
        editable? @(rf/subscribe [::patient.db/editable?])
        {:patient/keys [id
                        name
                        address
                        sex
                        gender
                        dob]} @(rf/subscribe [::patient.db/form])
        disabled (and existing-patient (not editable?))]
    [:> Grid {:container true
              :spacing 2
              :sx {:px 4
                   :py 2}}
     [:> Grid {:item true
               :xs 8}
      [:> Typography {:variant "h5"}
       (if existing-patient
         (str "Patient > " id)
         "New Patient")]]
     [:> Grid {:item true
               :xs 4}
      (when existing-patient
        [:> Button {:variant "outlined"
                    :full-width true
                    :on-click #(rf/dispatch [::patient.db/set-editable? (not editable?)])}
         (if editable?
           "Cancel"
           "Edit")])]
     [input {:xs 12
             :label "Name"
             :attr :patient/name
             :required true
             :value name
             :disabled disabled}]
     [input {:xs 12
             :label "Address"
             :attr :patient/address
             :value address
             :disabled disabled}]
     [select {:xs 6
              :label "Sex"
              :attr :patient/sex
              :value sex
              :required true
              :disabled disabled
              :options ["male" "female"]}]
     [select {:xs 6
              :label "Gender"
              :attr :patient/gender
              :value gender
              :disabled disabled
              :options ["man" "woman" "non-binary" "other"]}]
     [datepicker {:xs 12
                  :label "Date of Birth"
                  :attr :patient/dob
                  :value dob
                  :required true
                  :disabled disabled}]
     [:> Grid {:item true
               :xs 6}
      (when existing-patient
        [:> Button {:variant "contained"
                    :full-width true
                    :color "secondary"
                    :on-click #(rf/dispatch [::patient.db/delete-patient id])}
         "Delete"])]
     [:> Grid {:item true
               :xs 6}
      [:> Button {:variant "contained"
                  :full-width true
                  :on-click #(rf/dispatch [::patient.db/submit-patient-form])}
       "Submit"]]]))
