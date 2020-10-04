(ns learn-cljfx.core
  (:require [cljfx.api :as fx])
  (:gen-class)
  (:import [javafx.application Platform]))

(defn -main
  "Nothing here!"
  [& args]
  (println "Hello, CLJFX!"))
