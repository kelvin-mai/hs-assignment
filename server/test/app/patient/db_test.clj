(ns app.patient.db-test
  (:require [clojure.test :refer :all]
            [tick.core :as t]
            [app.test-utils :refer [use-system test-system]]
            [app.api.patient.db :as patient.db]))

(use-fixtures :once (use-system))

(deftest patient-crud-test
  (let [{ds :postgres/db} @test-system]
    (testing "can create patient"
      (let [test-patient (patient.db/create ds {:name "km"
                                                :sex "male"
                                                :dob (t/date)})]
        (is (= (:patient/name test-patient) "km"))
        (is (= (:patient/sex test-patient) "male"))
        (is (inst? (:patient/dob test-patient)))

        (testing "can get new patient"
          (let [read-patient (patient.db/get-by-id ds (:patient/id test-patient))]
            (is (= read-patient test-patient))))

        (testing "can update patient"
          (let [updated-patient (patient.db/update ds (:patient/id test-patient)
                                                   {:sex "female"
                                                    :gender "woman"})]
            (is (not= test-patient updated-patient))
            (is (= (:patient/id test-patient)
                   (:patient/id updated-patient)))
            (is (= (:patient/sex updated-patient) "female"))
            (is (= (:patient/gender updated-patient) "woman")))

          (testing "can delete patient"
            (patient.db/delete-by-id ds (:patient/id test-patient))
            (is (= (count (patient.db/get-all ds {})) 0))))))))
