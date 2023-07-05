(ns app.routing.coercion
  (:require [reitit.coercion.malli :as rm]
            [malli.transform :as mt]
            [tick.core :as t]
            [malli.util :as mu]))

(defn encode-date
  [_ _]
  {:enter #(if (inst? %)
             (t/date %)
             %)})

(defn decode-date
  [_ _]
  {:enter #(when (string? %)
             (try
               (t/date %)
               (catch Exception e
                 (str "Not a valid date: " e))))})

(defn custom-transformer []
  (mt/transformer
   {:encoders {:date {:compile encode-date}}}
   {:decoders {:date {:compile decode-date}}}))

(def json-transformer
  (mt/transformer
   mt/json-transformer
   custom-transformer
   mt/default-value-transformer))

(def string-transformer
  (mt/transformer
   mt/string-transformer
   custom-transformer
   mt/default-value-transformer))

(def coercion
  (rm/create
   (-> rm/default-options
       (assoc-in [:transformers :body :formats "application/json"] json-transformer)
       (assoc-in [:transformers :string :default] string-transformer)
       (assoc-in [:compile] mu/open-schema))))
