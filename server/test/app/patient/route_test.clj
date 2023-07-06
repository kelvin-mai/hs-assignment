(ns app.patient.route-test
  (:require [clojure.test :refer :all]
            [tick.core :as t]
            [app.test-utils :refer [use-system test-system
                                    request]]))

(use-fixtures :once (use-system))

(deftest patient-crud-test
  (let [{router :reitit/router} @test-system]
    (testing "can create patient"
      (let [test-patient-body {:name "km"
                               :sex "male"
                               :dob (t/date "1990-08-15")}
            test-patient (-> (request router
                                      :post "/api/patient"
                                      {:body-params test-patient-body})
                             :data)
            uri (str "/api/patient/" (:patient/id test-patient))]
        (is (= (:patient/name test-patient) "km"))
        (is (= (:patient/sex test-patient) "male"))

        (testing "can get patient"
          (let [read-patient (-> (request router
                                          :get uri)
                                 :data)]
            (is (= test-patient read-patient))))

        (testing "can update patient"
          (let [updated-patient (-> (request router
                                             :put uri
                                             {:body-params {:gender "man"}})
                                    :data)]
            (is (not= test-patient updated-patient))
            (is (= (:patient/id test-patient)
                   (:patient/id updated-patient)))
            (is (= (:patient/gender updated-patient) "man"))))

        (testing "can delete patient"
          (let [response (request router :delete uri)
                all-patients (-> (request router
                                          :get "/api/patient")
                                 :data)]
            (is (= (:success response) true))
            (is (= (count all-patients) 0))))))))
