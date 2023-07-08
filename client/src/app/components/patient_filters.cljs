(ns app.components.patient-filters
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string :as s]
            [app.db.patient :as patient.db]
            ["@mui/material" :refer [Accordion
                                     AccordionSummary
                                     AccordionDetails
                                     Button
                                     FormControl
                                     InputLabel
                                     MenuItem
                                     Grid
                                     Select
                                     TextField
                                     Typography]]
            ["@mui/icons-material" :refer [ExpandMore]]))

(defn input
  [{:keys [xs label attr value]}]
  (let []
    [:> Grid {:item true
              :xs xs}
     [:> TextField {:full-width true
                    :label label
                    :value (or value "")
                    :on-change #(rf/dispatch [::patient.db/set-filter-value
                                              attr (.. % -target -value)])}]]))

(defn select-options [value]
  ^{:key value}
  [:> MenuItem {:value value} (s/capitalize value)])

(defn select
  [{:keys [xs label attr value options]}]
  (let []
    [:> Grid {:item true
              :xs xs}
     [:> FormControl {:full-width true}
      [:> InputLabel label]
      [:> Select {:label label
                  :value (or value "")
                  :on-change #(rf/dispatch [::patient.db/set-filter-value
                                            attr (.. % -target -value)])}
       [:> MenuItem {:value nil} "Unselect"
        (map select-options options)]]]]))

(defn patient-filters []
  (let [{:patient/keys [name
                        address
                        sex
                        gender]} @(rf/subscribe [::patient.db/filters])]
    [:> Accordion
     [:> AccordionSummary
      "Search Criteria"]
     [:> AccordionDetails
      [:> Grid {:container true
                :spacing 2}
       [input {:xs 3
               :label "Name"
               :attr :patient/name
               :value name}]
       [input {:xs 3
               :label "Address"
               :attr :patient/address
               :value address}]
       [select {:xs 3
                :label "Sex"
                :attr :patient/sex
                :value sex
                :options ["male" "female"]}]
       [select {:xs 3
                :label "Gender"
                :attr :patient/gender
                :value gender
                :options ["man" "woman" "non-binary" "other"]}]
       [:> Grid {:item true
                 :xs 8}]
       [:> Grid {:item true
                 :xs 2}
        [:> Button {:variant "contained"
                    :color "secondary"
                    :full-width true
                    :on-click #(rf/dispatch [::patient.db/reset-filter-values])}
         "Clear"]]
       [:> Grid {:item true
                 :xs 2}
        [:> Button {:variant "contained"
                    :full-width true
                    :on-click #(rf/dispatch [::patient.db/search-patients])}
         "Search"]]]]]))
