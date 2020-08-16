(ns learn-clojure.interop
  (:import (java.awt Frame Color EventQueue)
           (java.io IOException)
           (java.lang Thread$UncaughtExceptionHandler)
           (java.awt.event KeyAdapter)
           (java.util ArrayList)))

; java constructor call
(type (String. "hello Java string"))

;instance method call
(let [string (String. "java")]
  (.charAt string 1))

; alt syntax (not as common
(let [string (String. "java")]
  (. string charAt 1))

; static method call
(System/getProperty "java.version")

; chain calling (eq: StringBuilder.append().append().toString())
(.. (StringBuffer.)
    (append "Running Java ")
    (append (System/getProperty "java.version"))
    (toString))

; doto allows multiple methods in one go
(let [frame (doto (Frame.)
              (.setLayout nil)
              (.setSize 400 300)
              (.setBackground Color/ORANGE)
              (.setVisible true))]
  ; create ref to frame in thread and close via event queue
  (.start (Thread. ((fn []
                      (Thread/sleep 2000)
                      (EventQueue/invokeLater ((fn [] (.dispose frame))))))))) ; so many parenthesis!

; handling exceptions from Java (gross)
(let [perform-io! (fn [] (throw (IOException. "purposely throwing!")))
      cleanup! (fn [] (println "clean-up!"))
      result (try
               (perform-io!)
               :success
               (catch IOException e
                 :io-error)
               (catch Exception e
                 :catch-all)
               (finally (cleanup!)))] result)

(Thread/setDefaultUncaughtExceptionHandler
  ; reify macro allow to create an anonymous class extending java.lang.Object
  ; class and/or implementing specified interfaces/protocols.
  (reify Thread$UncaughtExceptionHandler
    (uncaughtException [this thread throwable]
      (println (.getMessage throwable)))))

(Thread/setDefaultUncaughtExceptionHandler nil)

; implement abstract class (can be used on interface but shouldn't)
; method implementation has implicit 'this'
(proxy [KeyAdapter] []
  (keyPressed [event]
    (println "KeyPressed" this (.getKeyCode event))))

; adding fields? .. make a function that creates the arrayList and
; then do stuff with the methods implemented from the interface
(defn make-something []
  (let [newProperty (ArrayList.)]
    (reify Thread$UncaughtExceptionHandler
      (uncaughtException [this thread throwable]
        (println (.getMessage throwable))))))
