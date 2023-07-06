(ns app.api.patient.schema
  (:require [app.utils.schema :refer [nanoid?
                                      non-blank-string?
                                      date?
                                      sex?
                                      gender?]]
            [malli.util :as mu]))

(def id-path
  [:map
   [:id (nanoid?)]])

(def search-query
  (mu/optional-keys
   [:map
    [:attr :keyword]
    [:dir [:enum :asc :desc]]
    [:limit :int]
    [:offset :int]]))

(def create-body
  (mu/merge
   [:map
    [:name non-blank-string?]
    [:sex sex?]
    [:dob date?]]
   (mu/optional-keys
    [:map
     [:gender gender?]
     [:address non-blank-string?]])))

(def update-body
  (mu/optional-keys
   create-body))
