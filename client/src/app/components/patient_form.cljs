(ns app.components.patient-form
  (:require [re-frame.core :as rf]
            [clojure.string :as s]
            [app.db.patient :as patient.db]
            ["@mui/material" :refer [Button
                                     FormControl
                                     InputLabel
                                     MenuItem
                                     Grid
                                     Select
                                     TextField]]
            ["@mui/x-date-pickers/AdapterDayjs" :refer [AdapterDayjs]]
            ["@mui/x-date-pickers" :refer [LocalizationProvider
                                           DatePicker
                                           DateCalendar]]))

(defn input
  [{:keys [xs label attr value required]}]
  [:> Grid {:item true
            :xs xs}
   [:> TextField {:full-width true
                  :required required
                  :label label
                  :value (or value "")
                  :on-change #(rf/dispatch [::patient.db/set-form-value attr
                                            (.. % -target -value)])}]])

(defn select-options [value]
  ^{:key value}
  [:> MenuItem {:value value} (s/capitalize value)])

(defn select
  [{:keys [xs label attr value options required]}]
  [:> Grid {:item true
            :xs xs}
   [:> FormControl {:full-width true
                    :required required}
    [:> InputLabel label]
    [:> Select {:label label
                :value (or value "")
                :required required
                :on-change #(rf/dispatch [::patient.db/set-form-value attr
                                          (.. % -target -value)])}
     [:> MenuItem {:value nil} "Unselect"]
     (map select-options options)]]])

(defn datepicker [{:keys [xs label attr value required]}]
  [:> Grid {:item true
            :xs xs}
   [:> FormControl {:full-width true
                    :required required}
    [:> InputLabel label]
    [:> LocalizationProvider {:dateAdapter AdapterDayjs}
     [:> DateCalendar {:label label
                       :value value
                       :on-change #(rf/dispatch [::patient.db/set-form-value attr %])}]]]])

(defn patient-form []
  (let [{:keys [name
                address
                sex
                gender
                dob]} @(rf/subscribe [::patient.db/form])]
    [:> Grid {:container true
              :spacing 2
              :sx {:px 4
                   :py 2}}
     [input {:xs 12
             :label "Name"
             :attr :name
             :required true
             :value name}]
     [input {:xs 12
             :label "Address"
             :attr :address
             :value address}]
     [select {:xs 6
              :label "Sex"
              :attr :sex
              :value sex
              :required true
              :options ["male" "female"]}]
     [select {:xs 6
              :label "Gender"
              :attr :gender
              :value gender
              :options ["man" "woman" "non-binary" "other"]}]
     [datepicker {:xs 12
                  :label "Date of Birth"
                  :attr :dob
                  :value dob
                  :required true}]
     [:> Grid {:item true
               :xs 12}
      [:> Button {:variant "contained"
                  :full-width true
                  :on-click #(rf/dispatch [::patient.db/submit-patient-form])}
       "Submit"]]]))
