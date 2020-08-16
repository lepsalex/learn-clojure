(ns learn-clojure.fundamentals)

(println "Hello")

(= ["hello" 2 3] [(str "hell" "o") 2 3])

(not= ["hello" 2 3] [(str "hell" "o") 2 3])

(contains? #{"a" "b" "c" "hello"}
           (str "hel" "lo"))

(if (= "hell" (str "he" "llo"))
  "it is a hello"
  (do
    (println "this is logged but not returned")
    "returnValue"))

(= "1" 1)

(when (= "hello" (str "he" "llo"))
  (println "same as the if -> do")
  "still returns last entry")

(defn acceptable-number? [n]
  (if (or (odd? n)
          (< 10 n))
    "YES"
    "NO"))