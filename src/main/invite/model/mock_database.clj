(ns invite.model.mock-database
  "This is a mock database implemented via Datascript, which runs completely in memory, has few deps, and requires
  less setup than Datomic itself.  Its API is very close to Datomics, and for a demo app makes it possible to have the
  *look* of a real back-end without having quite the amount of setup to understand for a beginner."
  (:require
    [datascript.core :as d]
    [mount.core :refer [defstate]]))

;; In datascript just about the only thing that needs schema
;; is lookup refs and entity refs.  You can just wing it on
;; everything else.
(def schema {:user/id    {:db/cardinality :db.cardinality/one
                          :db/type        :db.type/uuid
                          :db/unique      :db.unique/identity}
             :user/name  {:db/type :db.type/string}

             :event/id   {:db/cardinality :db.cardinality/one
                          :db/type        :db.type/string
                          :db/unique      :db.unique/identity}
             :event/body {:db/type :db.type/string}})

(defn new-database [] (d/create-conn schema))

(defstate conn :start (new-database))
