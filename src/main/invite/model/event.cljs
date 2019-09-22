(ns invite.model.event
  (:require
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))

(defmutation attend-event [{:attendee/keys [id name]
                            :event/keys    [slug]}]
  (action [env] true)
  (remote [env] true))

#_(defmutation attend
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