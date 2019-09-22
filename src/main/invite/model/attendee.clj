(ns invite.model.attendee
  (:require
    [invite.model.mock-database :as db]
    [datascript.core :as d]
    [ghostwheel.core :refer [>defn => | ?]]
    [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
    [taoensso.timbre :as log]
    [clojure.spec.alpha :as s]))

(s/def :attendee/name (s/and string? not-empty))
(s/def ::attendee (s/keys :req [:attendee/id :attendee/name]))

(defn attendee-ident [id]
  [:attendee/id id])

(defresolver attendee-resolver [{:keys [db]} {:account/keys [id]}]
  {::pc/input  #{:account/id}
   ::pc/output [:account/name]}
  (d/pull db '[*] (attendee-ident id)))

(def resolvers [attendee-resolver])
