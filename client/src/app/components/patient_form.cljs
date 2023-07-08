(ns app.components.patient-form
  (:require [re-frame.core :as rf]
            [clojure.string :as s]
            [app.db.router :as router.db]
            [app.db.patient :as patient.db]
            ["@mui/material" :refer [Button
                                     FormControl
                                     FormHelperText
                                     InputLabel
                                     MenuItem
                                     Grid
                                     Select
                                     TextField
                                     Typography]]
            ["@mui/x-date-pickers/AdapterDayjs" :refer [AdapterDayjs]]
            ["@mui/x-date-pickers" :refer [LocalizationProvider
                                           DateCalendar]]))

(defn input
  [{:keys [xs label attr value required disabled show-validation?]}]
  (let [display-error? (and show-validation? (s/blank? value))]
    [:> Grid {:item true
              :xs xs}
     [:> TextField {:full-width true
                    :required required
                    :disabled disabled
                    :error display-error?
                    :helper-text (when display-error?
                                   "Incorrect entry")
                    :label label
                    :data-test-id (str attr)
                    :value (or value "")
                    :on-change #(rf/dispatch [::patient.db/set-form-value attr
                                              (.. % -target -value)])}]]))

(defn select-options [value]
  ^{:key value}
  [:> MenuItem {:value value} (s/capitalize value)])

(defn select
  [{:keys [xs label attr value options required disabled show-validation?]}]
  (let [display-error? (and show-validation? (s/blank? value))]
    [:> Grid {:item true
              :xs xs}
     [:> FormControl {:full-width true
                      :required required
                      :data-test-id (str attr)
                      :error display-error?}
      [:> InputLabel label]
      [:> Select {:label label
                  :value (or value "")
                  :disabled disabled
                  :on-change #(rf/dispatch [::patient.db/set-form-value attr
                                            (.. % -target -value)])}
       [:> MenuItem {:value nil} "Unselect"]
       (map select-options options)]
      (when display-error?
        [:> FormHelperText "Incorrect entry"])]]))

(defn datepicker [{:keys [xs label attr value required disabled show-validation?]}]
  [:> Grid {:item true
            :xs xs}
   [:> FormControl {:full-width true
                    :required required
                    :data-test-id (str attr)
                    :error (and show-validation? (nil? value))}
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
        disabled (and existing-patient (not editable?))
        show-validation? @(rf/subscribe [::patient.db/show-validation?])]
    [:> Grid {:container true
              :spacing 2
              :sx {:px 4
                   :py 2}}
     [:> Grid {:item true
               :xs 8}
      [:> Typography {:variant "h5"
                      :data-test-id "patient-form-title"}
       (if existing-patient
         (str "Patient > " id)
         "New Patient")]]
     [:> Grid {:item true
               :xs 4}
      (when existing-patient
        [:> Button {:variant "outlined"
                    :full-width true
                    :data-test-id "edit-patient"
                    :on-click #(rf/dispatch [::patient.db/set-editable? (not editable?)])}
         (if editable?
           "Cancel"
           "Edit")])]
     [input {:xs 12
             :label "Name"
             :attr :patient/name
             :required true
             :value name
             :disabled disabled
             :show-validation? show-validation?}]
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
              :show-validation? show-validation?
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
                  :show-validation? show-validation?
                  :disabled disabled}]
     [:> Grid {:item true
               :xs 6}
      (when existing-patient
        [:> Button {:variant "contained"
                    :full-width true
                    :color "secondary"
                    :data-test-id "delete-patient"
                    :on-click #(rf/dispatch [::patient.db/delete-patient id])}
         "Delete"])]
     [:> Grid {:item true
               :xs 6}
      [:> Button {:variant "contained"
                  :full-width true
                  :type "submit"
                  :on-click #(rf/dispatch [::patient.db/validate-submit-patient-form])}
       "Submit"]]]))
