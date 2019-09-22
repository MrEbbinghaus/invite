(ns invite.model.attendee
  (:require
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))

(defn user-path
  "Normalized path to a user entity or field in Fulcro state-map"
  ([id field] [:attandee/id id field])
  ([id] [:attandee/id id]))

(defn insert-user*
  "Insert a user into the correct table of the Fulcro state-map database."
  [state-map {:keys [:attandee/id] :as user}]
  (assoc-in state-map (user-path id) user))

(defmutation attend
  "Client Mutation: Upsert a user (full-stack. see CLJ version for server-side)."
  [{:keys [:attandee/id :attandee/name] :as params}]
  (action [{:keys [state]}]
    (log/info "Upsert user action")
    (swap! state (fn [s]
                   (-> s
                     (insert-user* params)
                     (merge/integrate-ident* [:attandee/id id] :append [:all-attandees])))))
  (ok-action [env]
    (log/info "OK action"))
  (error-action [env]
    (log/info "Error action"))
  (remote [env]
    (-> env
      (m/with-target (targeting/append-to [:all-attandees])))))
