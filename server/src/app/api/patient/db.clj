(ns app.api.patient.db
  (:require [honey.sql.helpers :as sqlh]
            [app.utils.db :as db]))

(defn- cast-enums [data]
  (cond-> data
    (:sex data) (assoc :sex [:cast (:sex data) :sex])
    (:gender data) (assoc :gender [:cast (:gender data) :gender])))

(defn- build-where
  [{:patient/keys [name
                   address
                   sex
                   gender]}]
  (let [name-clause [:ilike :patient.name (str "%" name "%")]
        address-clause [:ilike :patient.address (str "%" address "%")]
        sex-clause [:= :patient.sex [:cast sex :sex]]
        gender-clause [:= :patient.gender [:cast gender :gender]]]
    (when (or name address sex gender)
      (cond-> [:and]
        name (conj name-clause)
        address (conj address-clause)
        sex (conj sex-clause)
        gender (conj gender-clause)))))

(defn search-query
  [{:keys [attr dir limit offset] :as query-params}]
  (let [query {:select [:*
                        [[:over [[:count :patient.id]]] "total"]]
               :from :patient}
        where-clause (build-where query-params)]
    (cond-> query
      where-clause (assoc :where where-clause)
      (and attr dir) (sqlh/order-by [(keyword attr) (keyword dir) :null-last])
      limit (sqlh/limit limit)
      offset (sqlh/offset offset))))

(defn get-all
  [db q]
  (let [query (search-query q)]
    (db/query! db query)))

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
