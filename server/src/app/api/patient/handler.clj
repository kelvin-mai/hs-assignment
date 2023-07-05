(ns app.api.patient.handler
  (:require [app.api.patient.db :as patient.db]
            [app.api.patient.schema :as patient.schema]
            [app.routing.response :refer [ok created]]
            [app.routing.exception :refer [not-found]]))

(defn get-all-patients
  [{:keys [db]}]
  (let [response (patient.db/get-all db)]
    (ok response)))

(defn create-patient
  [{:keys [db parameters]}]
  (let [body (:body parameters)
        response (patient.db/create db body)]
    (created response)))

(defn get-patient-by-id
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :id])
        response (patient.db/get-by-id db id)]
    (if response
      (ok response)
      (not-found request))))

(defn delete-patient
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :id])
        response (patient.db/delete-by-id db id)]
    (if response
      (ok response)
      (not-found request))))

(defn update-patient
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :id])
        body (:body parameters)
        response (patient.db/update db id body)]
    (if response
      (ok response)
      (not-found request))))

(def routes
  ["/patient"
   ["/" {:get {:handler get-all-patients}
         :post {:parameters {:body patient.schema/create-body}
                :handler create-patient}}]
   ["/:id" {:parameters {:path patient.schema/id-path}
            :get get-patient-by-id
            :delete delete-patient
            :put {:parameters {:body patient.schema/update-body}
                  :handler update-patient}}]])
