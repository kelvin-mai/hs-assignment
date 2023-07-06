(ns app.components.patient-table
  (:require [re-frame.core :as rf]
            [tick.core :as t]
            [app.util.conversion :refer [same-sex-gender
                                         inst->date-string
                                         inst->datetime-string]]
            [app.db.patient :as patient.db]
            ["@mui/material" :refer [Table
                                     TableContainer
                                     TableHead
                                     TableRow
                                     TableCell
                                     TableBody
                                     TableSortLabel
                                     TablePagination]]))

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
                :sx {:cursor "pointer"}}
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

(defn patient-table []
  (rf/dispatch [::patient.db/fetch-patients])
  (fn []
    (let [patients @(rf/subscribe [::patient.db/patients])
          {:keys [page
                  rows-per-page]} @(rf/subscribe [::patient.db/pagination])]
      [:> TableContainer
       [:> Table {:sx {:min-width 650}}
        [:> TableHead
         (into [:> TableRow]
               (map patient-table-head patient-table-heads))]
        [patient-table-body patients]]
       [:> TablePagination {:rows-per-page-options [5 10 25]
                            :component "div"
                            :count (or (:total (first patients)) 0)
                            :rows-per-page rows-per-page
                            :page page
                            :on-page-change (fn [e new-page]
                                              (.preventDefault e)
                                              (rf/dispatch [::patient.db/set-page new-page]))
                            :on-rows-per-page-change #(rf/dispatch [::patient.db/set-rows-per-page
                                                                    (.. % -target -value)])}]])))
