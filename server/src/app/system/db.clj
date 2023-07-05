(ns app.system.db
  (:require [integrant.core :as ig]
            [hikari-cp.core :as hikari]
            [migratus.core :as migratus]))

(defmethod ig/init-key :postgres/db
  [_ {:keys [config]}]
  (let [options (:db config)
        datasource (hikari/make-datasource options)
        migration-config {:store :database
                          :migration-dir "migrations"
                          :db {:datasource datasource}}]
    (println "pending migrations:" (migratus/pending-list migration-config))
    (migratus/migrate migration-config)
    (println "completed migrations:" (migratus/completed-list migration-config))
    (println "starting database connection pool with options" options)
    datasource))

(defmethod ig/halt-key! :postgres/db
  [_ ds]
  (println "closing database connection pool")
  (hikari/close-datasource ds))
