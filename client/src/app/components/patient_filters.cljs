(ns app.components.patient-filters
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [app.components.common :refer [input
                                           select]]
            [app.db.patient :as patient.db]
            ["@mui/material" :refer [Accordion
                                     AccordionSummary
                                     AccordionDetails
                                     Button
                                     Grid]]
            ["@mui/icons-material" :refer [ExpandMore]]))

(defn patient-filters []
  (let [{:patient/keys [name
                        address
                        sex
                        gender]} @(rf/subscribe [::patient.db/filters])]
    [:> Accordion
     [:> AccordionSummary {:expand-icon (r/create-element ExpandMore)}
      "Search Criteria"]
     [:> AccordionDetails
      [:> Grid {:container true
                :spacing 2}
       [input {:xs 3
               :label "Name"
               :attr :patient/name
               :value name
               :on-change ::patient.db/set-filter-value}]
       [input {:xs 3
               :label "Address"
               :attr :patient/address
               :value address
               :on-change ::patient.db/set-filter-value}]
       [select {:xs 3
                :label "Sex"
                :attr :patient/sex
                :value sex
                :on-change ::patient.db/set-filter-value
                :options ["male" "female"]}]
       [select {:xs 3
                :label "Gender"
                :attr :patient/gender
                :value gender
                :on-change ::patient.db/set-filter-value
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
