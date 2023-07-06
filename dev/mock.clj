(ns mock
  (:require [app.api.patient.db :as patient.db]
            [tick.core :as t]
            [talltale.core :as fake]))

(defn random-age-date []
  (let [age (+ 1 (rand-int 100))
        now-year (.getValue (t/year))
        birth-year (- now-year age)]
    (t/new-date birth-year (+ 1 (rand-int 11)) (+ 1 (rand-int 28)))))

(defn random-gender []
  (let [num (rand-int 5)
        num-to-enum {0 nil
                     1 "man"
                     2 "woman"
                     3 "non-binary"
                     4 "other"}]
    (get num-to-enum num)))

(defn random-patient []
  (let [gender (random-gender)
        first-name (case gender
                     "man" (fake/first-name-male)
                     "woman" (fake/first-name-female)
                     (fake/first-name))
        last-name (fake/last-name)
        sex (if (= :male (fake/sex))
              "male"
              "female")
        address (fake/address)]
    {:name (str first-name " " last-name)
     :sex sex
     :gender gender
     :dob (random-age-date)
     :address (str (:street-number address) " " (:street address))}))

(defn insert-random-patient [db]
  (let [patient (random-patient)]
    (patient.db/create db patient)))

;; potentially unsafe because not within transaction
(defn insert-random-patient-multi [db n]
  (take n (repeatedly #(insert-random-patient db))))

(comment
  (insert-random-patient user/db)
  (insert-random-patient-multi user/db 100))
