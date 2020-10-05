(ns learn-cljfx.renderer
  (:require [cljfx.api :as fx]))

(def renderer
  (fx/create-renderer))

(defn root [{:keys [showing]}]
  {:fx/type :stage
   :showing showing
   :scene   {:fx/type :scene
             :root    {:fx/type  :v-box
                       :padding  50
                       :children [{:fx/type   :button
                                   :text      "close"
                                   :on-action (fn [_]
                                                (renderer {:fx/type root
                                                           :showing false}))}]}}})

(renderer {:fx/type root
           :showing true})