(ns invite.ui.root
  (:require
    [invite.model.session :as session]
    [invite.model.event :as event]
    [clojure.string :as str]
    [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h3 button]]
    [com.fulcrologic.fulcro.dom.html-entities :as ent]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [com.fulcrologic.fulcro.ui-state-machines :as uism :refer [defstatemachine]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro-css.css :as css]
    [com.fulcrologic.fulcro.algorithms.form-state :as fs]
    [taoensso.timbre :as log]
    ["react-dom" :as R]
    ["jquery" :as $]
    [goog.object :as gobj]
    [clojure.string :refer [escape]]
    [markdown.core :as markdown]
    [com.fulcrologic.fulcro.data-fetch :as df]))

(defsc AttendeeInput [this {:attendee/keys [name]} {:event/keys [slug]}]
  {:query         [:form/id :attendee/name fs/form-config-join]
   :ident         (fn [] [:form/id :new-attendee])
   :form-fields   #{:attendee/name}
   :initial-state (fn [_]
                    (fs/add-form-config AttendeeInput
                      {:attendee/name ""}))}
  (dom/form
    {:onSubmit (fn submit-attendee-form [e]
                 (.preventDefault e)
                 (comp/transact! this [(event/attend-event {:event/slug    slug
                                                            :attendee/id   (random-uuid)
                                                            :attendee/name name})]))}
    (dom/div :.form-group
      (dom/label "Wie lautet dein Name?"
        (dom/input :.form-control
          {:type        "text"
           :placeholder "Name"
           :value       name
           :onChange    #(m/set-string! this :attendee/name :event %)})))
    (button :.btn.btn-light {:type "submit"} "Anmelden")))

(def ui-attendee-input (comp/factory AttendeeInput))

(defsc AttendeeListItem [_this {:attendee/keys [name]}]
  {:query [:attendee/id :attendee/name]
   :ident :attendee/id}
  (dom/li :.list-group-item (clojure.string/capitalize name)))

(def ui-attendee-list-entry (comp/factory AttendeeListItem))

(defn markdown-render [content]
  (dom/div {:dangerouslySetInnerHTML {:__html (markdown/md->html content)}}))

(defn frosted-sheet-modal [ref & {:keys [head body]}]
  (div :.modal.frosted-sheet.fade
    {:ref      ref
     :tabIndex -1
     :role     "dialog"}
    (div :.spacer-frame
      (div :.spacer)
      (div :.modal-dialog
        (div :.modal-content
          (when head
            (div :.modal-header head))
          (div :.modal-body.container
            body)
          (button :.close
            {:type         "button"
             :aria-label   "Close"
             :data-dismiss "modal"}
            (dom/span {:aria-hidden true} "×")))))))

(defsc Event [this {:event/keys [slug content attendee-input attendees]}]
  {:query          [:event/slug
                    :event/content
                    {:event/attendee-input (comp/get-query AttendeeInput)}
                    {:event/attendees (comp/get-query AttendeeListItem)}]
   :ident          :event/slug
   :route-segment  [:event/slug]
   :initial-state  {:event/slug           "hacktoberfest2019"
                    :event/content        "# Hacktoberfest 2019\nBald geht es wieder los. Der Oktober ist ganz der Open-Source Community gewidmet.\n \nWir bieten Zeit und Raum um zusammen Dinge zu coden. \n\nLorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est "
                    :event/attendee-input {}}
   :initLocalState (fn [this _]
                     {:attend-modal-ref        (fn [r] (gobj/set this "attend-modal" r))
                      :attendee-list-modal-ref (fn [r] (gobj/set this "attendee-list-modal" r))})}
  (let [max-height "150px"]
    (div :.container.pt-3.pt-md-5
      {:style {:textAlign "center"}}
      (div :.text-white.row
        {:style {:paddingBottom max-height}}
        (markdown-render content))

      (div :.bottom.container-fluid.mt-auto.fixed-bottom
        {:style {:display              "block"
                 :left                 "0px"
                 :right                "0px"
                 :padding              "20px"
                 :maxHeight            max-height
                 :WebkitBackdropFilter "blur(10px)"
                 :backdropFilter       "blur(10px)"}}
        (div :.row.justify-content-center
          (button :.btn.btn-lg.btn-primary.m-1
            {:onClick #(-> this (gobj/get "attend-modal") $ (.modal "show"))} "Teilnehmen"))
        (div :.row.justify-content-center
          (button :.btn.btn-secondary.m-1
            {:onClick (fn [_e]
                        (df/load-field! this :event/attendees {})
                        (-> this (gobj/get "attendee-list-modal") $ (.modal "show")))}
            "Wer nimmt teil?")))

      (frosted-sheet-modal
        (comp/get-state this :attend-modal-ref)
        :body (ui-attendee-input (comp/computed attendee-input {:event/slug slug})))

      (frosted-sheet-modal
        (comp/get-state this :attendee-list-modal-ref)
        :head (h3 "Teilnehmer")
        :body (dom/ul :.list-group
                (->> attendees
                  (sort-by (comp clojure.string/capitalize :attendee/name))
                  (map ui-attendee-list-entry)))))))

(dr/defrouter TopRouter [this props]
  {:router-targets [Event]})

(def ui-top-router (comp/factory TopRouter))

(defsc Root [this {:root/keys [router]}]
  {:query         [{:root/router (comp/get-query TopRouter)}]
   :ident         (fn [] [:component/id :ROOT])
   :initial-state {:root/router {}}
   :css           [[:.root {:min-height "100vh"}]]}
  (div :.root.disco {:classes [(:root (css/get-classnames Root))]}
    (ui-top-router router)))

