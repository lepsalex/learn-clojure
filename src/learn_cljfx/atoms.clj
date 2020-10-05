(ns learn-cljfx.atoms
  (:require [cljfx.api :as fx]))

;; Define application state

(def *state
  (atom {:title "Type to Replace Me!"}))

;; Define render functions

(defn title-input [{:keys [title]}]
  {:fx/type         :text-field
   :on-text-changed #(swap! *state assoc :title %)
   :text            title})

(defn root [{:keys [title]}]
  {:fx/type :stage
   :showing true
   :title   title
   :scene   {:fx/type :scene
             :root    {:fx/type  :v-box
                       :children [{:fx/type :label
                                   :text    "Window Title Output:"}
                                  {:fx/type title-input
                                   :title   title}]}}})

;; Create renderer with middleware that maps incoming data - description -
;; to component description that can be used to render JavaFX state.
;; Here description is just passed as an argument to function component.

(def renderer
  (fx/create-renderer
    :middleware (fx/wrap-map-desc assoc :fx/type root)))

;; Convenient way to add watch to an atom + immediately render app

(fx/mount-renderer *state renderer)