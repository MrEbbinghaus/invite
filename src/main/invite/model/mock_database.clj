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
(def schema {:attendee/id    {:db/ident :attendee/id
                              :db/cardinality :db.cardinality/one
                              :db/type        :db.type/uuid
                              :db/unique      :db.unique/identity}
             :attendee/name  {:db/type :db.type/string}

             :event/slug   {:db/cardinality :db.cardinality/one
                            :db/type        :db.type/string
                            :db/unique      :db.unique/identity}
             :event/content {:db/type :db.type/string}
             :event/attendees {:db/type :db.type/ref
                               :db/cardinality :db.cardinality/many}})

(defn new-database [] (d/create-conn schema))

(defn example-db [conn]
  (d/transact! conn [#:event{:slug "hacktoberfest2019"
                             :content "# Hacktoberfest\n\nYeah!"
                             :attendees []}]))

(defstate conn :start
  (let [c (new-database)]
    (example-db c)
    c))
