(ns invite.model.event
  (:require
    [invite.model.mock-database :as db]
    [datascript.core :as d]
    [ghostwheel.core :refer [>defn => | ?]]
    [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
    [taoensso.timbre :as log]
    [clojure.spec.alpha :as s]))

(defn event-ident [slug]
  [:event/slug slug])

(defresolver event-details [{:keys [db]} {:event/keys [slug]}]
  {::pc/input  #{:event/slug}
   ::pc/output [:event/slug :event/content]}
  (d/pull db '[*] (event-ident slug)))

(defresolver event-attendees [{:keys [db]} {:event/keys [slug]}]
  {::pc/input  #{:event/slug}
   ::pc/output [{:event/attendees [:attendee/id]}]}
  (or
    (d/pull db '[{:event/attendees [:attendee/id]}] (event-ident slug))
    {:event/attendees []}))

(>defn tx-attend-to-event [slug {:attendee/keys [id] :as attendee}]
  [(s/and string? not-empty) :invite.model.attendee/attendee => vector?]
  [attendee [:db/add (event-ident slug) :event/attendees [:attendee/id id]]])

(defmutation attend-event [{:keys [connection]} {:attendee/keys [id name]
                                                 :event/keys    [slug]}]
  {::pc/params [:event/slug :attendee/id :attendee/name]
   ::pc/output [:event/slug]}
  (do (d/transact! connection (tx-attend-to-event slug #:attendee{:id id :name name}))
      {:event/slug slug}))

(def resolvers [event-details event-attendees attend-event])