(ns app.api.patient.db
  (:require [clojure.string :as s]
            [honey.sql.helpers :as sqlh]
            [app.utils.db :as db]))

(defn- cast-enums [data]
  (cond-> data
    (:sex data) (assoc :sex [:cast (:sex data) :sex])
    (:gender data) (assoc :gender [:cast (:gender data) :gender])))

(defn- build-gender-clause [gender]
  [:or
   [:= :patient.gender [:cast gender :gender]]
   (when (or (= gender "man") (= gender "woman"))
     [:and
      [:= :patient.gender nil]
      (if (= gender "man")
        [:= :patient.sex [:cast "male" :sex]]
        [:= :patient.sex [:cast "female" :sex]])])])

(defn- build-age-clause [age]
  (let [[min-age max-age] (when age
                            (case age
                              "under 5" [0 5]
                              "65 and older" [65 200]
                              (s/split age #" - ")))]
    [:and
     [:<= :patient.dob [:- [:now] [:cast (str min-age " years") :interval]]]
     [:>= :patient.dob [:- [:now] [:cast (str max-age " years") :interval]]]]))

(defn- build-where
  [{:patient/keys [name
                   address
                   sex
                   gender
                   age] :as t}]
  (let [_ (def temp t)
        name-clause [:ilike :patient.name (str "%" name "%")]
        address-clause [:ilike :patient.address (str "%" address "%")]
        sex-clause [:= :patient.sex [:cast sex :sex]]
        gender-clause (build-gender-clause gender)
        age-clause (build-age-clause age)]
    (when (or name address sex gender age)
      (cond-> [:and]
        name (conj name-clause)
        address (conj address-clause)
        sex (conj sex-clause)
        gender (conj gender-clause)
        age (conj age-clause)))))

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
