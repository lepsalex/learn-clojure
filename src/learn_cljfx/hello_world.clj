(ns learn-cljfx.hello-world
  (:require [cljfx.api :as fx]))

(fx/on-fx-thread
  (fx/create-component
    {:fx/type :stage
     :showing true
     :title   "CLJFX Example"
     :width   300
     :height  300
     :scene   {:fx/type :scene
               :root    {:fx/type   :v-box
                         :alignment :center
                         :children  [{:fx/type :label
                                      :text    "Hello World!"}]}}}))



