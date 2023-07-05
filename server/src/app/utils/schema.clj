(ns app.utils.schema
  (:require [malli.core :as m]
            [tick.core :as t]
            [clojure.string :as s]))

(defn nanoid?
  ([] (nanoid? 16))
  ([l]
   (m/-simple-schema
    {:type (keyword (str "nanoid-" l))
     :pred #(and (string? %)
                 (boolean (re-matches #"^[a-zA-Z0-9-_]+$" %))
                 (= (count %) l))})))

(def non-blank-string?
  (m/-simple-schema
   {:type :non-blank-string
    :pred #(and (string? %)
                (not (s/blank? %)))}))

(def date?
  (m/-simple-schema
   {:type :date
    :pred t/date?}))

(def sex?
  [:enum "male" "female"])

(def gender?
  [:enum "man" "woman" "non-binary" "other"])
