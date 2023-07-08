(ns app.components.common
  (:require [re-frame.core :as rf]
            [clojure.string :as s]
            [app.util.conversion :refer [v]]
            ["@mui/material" :refer [FormControl
                                     FormHelperText
                                     InputLabel
                                     MenuItem
                                     Grid
                                     Select
                                     TextField]]))

(defn input
  [{:keys [xs label attr value on-change
           required disabled show-validation?]}]
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
                    :on-change #(rf/dispatch [on-change attr (v %)])}]]))

(defn select-options [value]
  ^{:key value}
  [:> MenuItem {:value value} (s/capitalize value)])

(defn select
  [{:keys [xs label attr value options on-change
           required disabled show-validation?]}]
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
                  :on-change #(rf/dispatch [on-change attr (v %)])}
       [:> MenuItem {:value nil} "Unselect"]
       (map select-options options)]
      (when display-error?
        [:> FormHelperText "Incorrect entry"])]]))
