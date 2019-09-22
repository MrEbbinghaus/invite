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
    [markdown.core :as markdown]))

(defsc AttendeeInput [this {:keys [form/id] :attendee/keys [name]} {:event/keys [slug]}]
  {:query [:form/id :attendee/name fs/form-config-join]
   :ident (fn [] [:form/id :new-attendee])
   :form-fields  #{:attendee/name}
   :initial-state (fn [_]
                    (fs/add-form-config AttendeeInput
                      {:attendee/name ""}))}
  (dom/form
    {:onSubmit (fn submit-attendee-form [e]
                 (.preventDefault e)
                 (js/console.log e)
                 (comp/transact! this [(event/attend-event {:event/slug slug
                                                            :attendee/id (random-uuid)
                                                            :attendee/name name})]))}
    (dom/div :.form-group
      (dom/label "Wie lautet dein Name?"
        (dom/input :.form-control
          {:type "text"
           :placeholder "Name"
           :value name
           :onChange #(m/set-string! this :attendee/name :event %)})))
    (button :.btn.btn-light {:type "submit"} "Absenden")))

(def ui-attendee-input (comp/factory AttendeeInput))

(defn markdown-render [content]
  (dom/div {:dangerouslySetInnerHTML {:__html (markdown/md->html content)}}))

(defsc Event [this {:event/keys [slug content attendee-input] :as props}]
  {:query         [:event/slug
                   :event/content
                   {:event/attendee-input (comp/get-query AttendeeInput)}]
   :ident         :event/slug
   :route-segment [:event/slug]
   :initial-state {:event/slug "hacktoberfest2019"
                   :event/content "# Hacktoberfest 2019\nBald geht es wieder los. Der Oktober ist ganz der Open-Source Community gewidmet.\n \nWir bieten Zeit und Raum um zusammen Dinge zu coden. \n\nLorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est "
                   :event/attendee-input {}}
   :initLocalState     (fn [this _]
                         {:attend-modal-ref (fn [r] (gobj/set this "attend-modal" r))})}
  (let [max-height "150px"
        attend-modal-refn (comp/get-state this :attend-modal-ref)]
    (div :.container.pt-3.pt-md-5
      {:style {:text-align "center"}}
      (div :.text-white.row
        {:style {:padding-bottom max-height}}
        (markdown-render content))

      (div :.bottom.container-fluid.mt-auto.fixed-bottom
        {:style {:display "block"
                 :left "0px"
                 :right "0px"
                 :padding "20px"
                 :max-height "max-height"
                 :-webkit-backdrop-filter "blur(10px)";
                 :backdrop-filter "blur(10px)"}}
        (div :.row.justify-content-center
          (button :.btn.btn-lg.btn-primary.m-1
            {:onClick #(-> this (gobj/get "attend-modal") $ (.modal "show"))} "Teilnehmen"))
        (div :.row.justify-content-center
          (button :.btn.btn-secondary.m-1
            {:onClick #(-> this (gobj/get "modal") $ (.modal "show"))} "Wer nimmt sonst teil?")))


      (div :.modal.frosted-sheet.fade
        {:ref attend-modal-refn
         :tabIndex -1
         :role "dialog"}
        (div :.modal-dialog
          (div :.modal-content
            (div :.modal-body.container
              (ui-attendee-input (comp/computed attendee-input {:event/slug slug})))
            (button :.close
              {:type "button"
               :aria-label "Close"
               :data-dismiss "modal"}
              (dom/span {:aria-hidden true} "×"))))))))

(dr/defrouter TopRouter [this props]
  {:router-targets [Event]})

(def ui-top-router (comp/factory TopRouter))

(defsc Root [this {:root/keys [router]}]
  {:query         [{:root/router (comp/get-query TopRouter)}]
   :ident         (fn [] [:component/id :ROOT])
   :initial-state {:root/router {}}
   :css [[:.root {:min-height "100vh"}]]}
  (div :.root.disco {:classes [(:root (css/get-classnames Root))]}
    (ui-top-router router)))

