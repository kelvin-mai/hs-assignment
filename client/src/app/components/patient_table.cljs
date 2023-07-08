(ns app.components.patient-table
  (:require [re-frame.core :as rf]
            [app.util.conversion :refer [same-sex-gender
                                         inst->date-string
                                         inst->datetime-string]]
            [app.components.patient-filters :refer [patient-filters]]
            [app.db.patient :as patient.db]
            [app.db.router :as router.db]
            ["@mui/material" :refer [Box
                                     Button
                                     Table
                                     TableContainer
                                     TableHead
                                     TableRow
                                     TableCell
                                     TableBody
                                     TableSortLabel
                                     TablePagination
                                     Typography]]))

(def patient-table-heads
  [[:patient/id "ID"]
   [:patient/name "Name"]
   [:patient/sex "Sex"]
   [:patient/gender "Gender"]
   [:patient/dob "Date of Birth"]
   [:patient/address "Address"]
   [:patient/created "Created"]])

(defn patient-table-head [[kw s]]
  (let [{:keys [attr dir]} @(rf/subscribe [::patient.db/pagination])]
    ^{:key kw}
    [:> TableCell
     [:> TableSortLabel {:active (= attr kw)
                         :direction (or dir :asc)
                         :on-click #(rf/dispatch [::patient.db/set-sort kw])}
      s]]))

(defn patient-table-row [{:patient/keys [id name sex gender dob address created]}]
  ^{:key id}
  [:> TableRow {:hover true
                :sx {:cursor "pointer"}
                :on-click #(rf/dispatch [::router.db/push-state :app.router/existing-patient {:id id}])}
   [:> TableCell id]
   [:> TableCell name]
   [:> TableCell sex]
   [:> TableCell (or gender (same-sex-gender sex))]
   [:> TableCell (inst->date-string dob)]
   [:> TableCell address]
   [:> TableCell (inst->datetime-string created)]])

(defn patient-table-body [rows]
  (if rows
    (into
     [:> TableBody]
     (map patient-table-row rows))
    [:<>]))

(defn empty-patients []
  [:> Box {:width "100%"
           :p 10
           :flex true
           :text-align "center"}
   [:> Typography {:variant "p"
                   :component "div"
                   :sx {:mb 2}}
    "No patients currently exist with this search criteria."]
   [:> Button {:variant "outlined"
               :on-click #(rf/dispatch [::router.db/push-state :app.router/new-patient])
               :data-test-id "empty-patients-create"}
    "Add new patient"]])

(defn patient-table []
  (let [patients @(rf/subscribe [::patient.db/patients])
        {:keys [page
                rows-per-page]} @(rf/subscribe [::patient.db/pagination])]
    [:<>
     [patient-filters]
     [:> TableContainer
      [:> Table {:sx {:min-width 650}}
       [:> TableHead
        (into [:> TableRow]
              (map patient-table-head patient-table-heads))]
       [patient-table-body patients]]
      (if (empty? patients)
        [empty-patients]
        [:> TablePagination {:rows-per-page-options [5 10 25]
                             :component "div"
                             :count (or (:total (first patients)) 0)
                             :rows-per-page rows-per-page
                             :page page
                             :on-page-change (fn [e new-page]
                                               (.preventDefault e)
                                               (rf/dispatch [::patient.db/set-page new-page]))
                             :on-rows-per-page-change #(rf/dispatch [::patient.db/set-rows-per-page
                                                                     (.. % -target -value)])}])]]))
