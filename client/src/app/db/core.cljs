(ns app.db.core
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]))

(def base-url "http://localhost:8080")

(def app-db
  {})

(rf/reg-event-db
 :initialize-db
 (fn [] app-db))

(rf/reg-event-fx
 :http
 (fn [_ [_ {:keys [method url data headers on-success on-failure]}]]
   {:http-xhrio {:method (or method :get)
                 :uri (str base-url url)
                 :params data
                 :headers headers
                 :timeout 5000
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success on-success
                 :on-failure (or on-failure [:http-failure])}}))

(rf/reg-event-db
 :http-failure
 (fn [db [_ {:keys [response]}]]
   (js/console.log :http-failure response)))