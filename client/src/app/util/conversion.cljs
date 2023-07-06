(ns app.util.conversion
  (:require [tick.core :as t]))

(defn stringify-kw [kw]
  (str (when (namespace kw)
         (str (namespace kw) "/"))
       (name kw)))

(defn same-sex-gender [sex]
  (if (= sex "male")
    "man"
    "woman"))

(defn inst->date-string [s]
  (->> s
       (t/instant)
       (t/date)
       (str)))

(defn inst->datetime-string [s]
  (let [inst (t/instant s)]
    (str (t/date inst) " " (t/time inst))))
