(ns app.api.patient.db
  (:require [app.utils.db :as db]))

(defn- cast-enums [data]
  (cond-> data
    (:sex data) (assoc :sex [:cast (:sex data) :sex])
    (:gender data) (assoc :gender [:cast (:gender data) :gender])))

(defn get-all
  [db]
  (db/query! db
             {:select [:*]
              :from :patient}))

(defn get-by-id
  [db id]
  (db/query-one! db
                 {:select [:*]
                  :from :patient
                  :where [:= :patient.id id]}))

(defn create
  [db data]
  (let [data (cast-enums data)]
    (db/query-one! db
                   {:insert-into :patient
                    :values [data]})))

(defn update
  [db id data]
  (let [data (cast-enums data)]
    (db/query-one! db
                   {:update :patient
                    :set data
                    :where [:= :patient.id id]})))

(defn delete-by-id
  [db id]
  (db/query-one! db
                 {:delete-from :patient
                  :where [:= :patient.id id]}))
