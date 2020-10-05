(ns learn-cljfx.todo
  (:require [cljfx.api :as fx])
  (:import [javafx.scene.input KeyCode KeyEvent]))

;; Define state

(def *state
  (atom {:typed-text ""
         :by-id      {0 {:id   0
                         :text "Buy milk"
                         :done true}
                      1 {:id   1
                         :text "Buy socks"
                         :done false}}}))

;; Define view as just data

(defn todo-view [{:keys [text id done]}]
  {:fx/type  :h-box
   :spacing  5
   :padding  5
   :children [{:fx/type             :check-box
               :selected            done
               :on-selected-changed {:event/type ::set-done :id id}} ; event mapping here
              {:fx/type :label
               :style   {:-fx-text-fill (if done :grey :black)}
               :text    text}]})

;; Root element

(defn root [{:keys [by-id typed-text]}]
  {:fx/type :stage
   :showing true
   :scene   {:fx/type :scene
             :root    {:fx/type     :v-box
                       :pref-width  300
                       :pref-height 400
                       :children    [{:fx/type      :scroll-pane
                                      :v-box/vgrow  :always
                                      :fit-to-width true
                                      :content      {:fx/type  :v-box
                                                     :children (->> by-id ; thread-last operator
                                                                    vals ; get values from threaded by-id
                                                                    (sort-by (juxt :done :id)) ; sort-by, with vals output as last arg
                                                                    (map #(assoc %
                                                                            :fx/type todo-view
                                                                            :fx/key (:id %))))}} ; and finally get our new map
                                     {:fx/type         :text-field
                                      :v-box/margin    5
                                      :text            typed-text
                                      :prompt-text     "Add a todo ... press Enter"
                                      :on-text-changed {:event/type ::type}
                                      :on-key-pressed  {:event/type ::press}}]}}})

;; Define single map-event-handler that does mutation

(defn map-event-handler [event]
  (case (:event/type event)
    ::set-done (swap! *state assoc-in [:by-id (:id event) :done] (:fx/event event))
    ::type (swap! *state assoc :typed-text (:fx/event event))
    ::press (when (= KeyCode/ENTER (.getCode ^KeyEvent (:fx/event event)))
              (swap! *state #(-> % ; thread-first (hover to get awesome example
                                 (assoc :typed-text "")
                                 (assoc-in [:by-id (count (:by-id %))]
                                           {:id   (count (:by-id %))
                                            :text (:typed-text %)
                                            :done false}))))
    nil))


;; Provide map-event-handler to renderer as an option

(fx/mount-renderer
  *state
  (fx/create-renderer
    :middleware (fx/wrap-map-desc assoc :fx/type root)
    :opts {:fx.opt/map-event-handler map-event-handler}))