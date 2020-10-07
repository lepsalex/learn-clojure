(ns learn-cljfx.context
  (:require [cljfx.api :as fx]
            [clojure.string :as string]
            [clojure.core.cache :as cache])
  (:import [javafx.scene.input KeyCode KeyEvent]))

;; The TO_DO app but with context instead of one big state

(def *state
  (atom
    (fx/create-context
      {:typed-text ""
       :tasks      {0 {:id   0
                       :text "Buy milk"
                       :done true}
                    1 {:id   1
                       :text "Buy socks"
                       :done false}}}
      cache/lru-cache-factory)))


;; Sub functions

(defn task-count [context]
  (count (fx/sub-val context :tasks)))

(defn remaining-tasks-count [context]
  (count (filter (fn [[k, v]] (identity (:done v))) (fx/sub-val context :tasks))))

;; Indirect subscription function that depends on 2 previously defined subscription
;; functions, which means that whenever value returned by `task-count` or
;; `remaining-task-count` changes, subscribing to this function will lead to a call
;; instead of cache lookup
(defn task-summary [context]
  (prn :task-summary)
  (format "Tasks: %d/%d"
          (fx/sub-ctx context remaining-tasks-count)
          (fx/sub-ctx context task-count)))


;; event handling

(defmulti event-handler :event/type)

(defmethod event-handler ::set-done [{:keys [task-id fx/event]}]
  (swap! *state fx/swap-context assoc-in [:tasks task-id :done] event))

(defmethod event-handler ::type [{:keys [fx/event]}]
  (swap! *state fx/swap-context assoc :typed-text event))

(defmethod event-handler ::press [{:keys [fx/event]}]
  (when (and (not (string/blank? (fx/sub-val @*state :typed-text))) (= KeyCode/ENTER (.getCode ^KeyEvent event)))
    (swap! *state fx/swap-context #(-> %                    ; thread-first (hover to get awesome example
                                       (assoc :typed-text "")
                                       (assoc-in [:tasks (count (:tasks %))]
                                                 {:id   (count (:tasks %))
                                                  :text (:typed-text %)
                                                  :done false}))))
  nil)

(defmethod event-handler :default [x] (prn x))


;; Define view as just data

(defn todo-view [{:keys [text id done]}]
  {:fx/type  :h-box
   :spacing  5
   :padding  5
   :children [{:fx/type             :check-box
               :selected            done
               :on-selected-changed {:event/type ::set-done :task-id id}} ; event mapping here
              {:fx/type :label
               :style   {:-fx-text-fill (if done :grey :black)}
               :text    text}]})

(defn todo-list-view [{:keys [fx/context]}]
  {:fx/type      :scroll-pane
   :fit-to-width true
   :content      {:fx/type  :v-box
                  :children (->> (fx/sub-val context :tasks) ; thread-last operator
                                 vals                       ; get values from threaded by-id
                                 (sort-by (juxt :done :id)) ; sort-by, with vals output as last arg
                                 (map #(assoc %
                                         :fx/type todo-view
                                         :fx/key (:id %))))}}) ; and finally get our new map


(defn text-box [{:keys [fx/context]}]
  {:fx/type         :text-field
   :text            (fx/sub-val context :typed-text)
   :prompt-text     "Add a todo ... press Enter"
   :on-text-changed {:event/type ::type}
   :on-key-pressed  {:event/type ::press}})


;; Root element

(defn root [_]
  {:fx/type :stage
   :showing true
   :scene   {:fx/type :scene
             :root    {:fx/type     :v-box
                       :pref-width  300
                       :pref-height 400
                       :children    [{:fx/type     todo-list-view
                                      :v-box/vgrow :always}
                                     {:fx/type      text-box
                                      :v-box/margin 5}]}}})


;; Provide map-event-handler to renderer as an option

(def renderer
  (fx/create-renderer
    :middleware (comp
                  fx/wrap-context-desc
                  (fx/wrap-map-desc (fn [_] {:fx/type root})))
    :opts {:fx.opt/map-event-handler event-handler
           :fx.opt/type->lifecycle   #(or (fx/keyword->lifecycle %)
                                          (fx/fn->lifecycle-with-context %))}))

(fx/mount-renderer *state renderer)