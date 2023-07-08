(ns app.util.conversion
  (:require [clojure.string :as s]
            [tick.core :as t]
            ["dayjs" :as dayjs]))

(defn stringify-kw [kw]
  (str (when (namespace kw)
         (str (namespace kw) "/"))
       (name kw)))

(defn same-sex-gender [sex]
  (if (= sex "male")
    "man"
    "woman"))

(defn inst->date-string [s]
  (when s
    (->> s
         (t/instant)
         (t/date)
         (str))))

(defn inst->datetime-string [s]
  (when s
    (let [inst (t/instant s)]
      (str (t/date inst) " " (t/truncate (t/time inst) :minutes)))))

(defn remove-nils [m]
  (into {} (remove (comp s/blank? second) m)))

(defn dayjs->date-string [d]
  (when d
    (.format (dayjs d) "YYYY-MM-DD")))

(defn inst-string->dayjs [d]
  (when d
    (dayjs d)))

(defn v [e]
  (.. e -target -value))
