(ns learn-clojure.functional
  (:require [clojure.string :as string]))

; list for use
(def names `("alex" "bob" "ben" "james" "peter" "sam" "sarah" "lex"))

; function as arg
(defn transform-random-name [transform-fn]
  (transform-fn (rand-nth names)))

; return a func
(defn make-adder [x]
  (fn [y]
    (println x "+" y)
    (+ x y)))

(def increment (make-adder 1))
(def decrement (make-adder -1))

; return is actually a lazy sequence
(take 2 [1 2 3 4 5])
(drop 2 [1 2 3 4 5])

; iterate can go forever, lazy evaluated
(take 10 (iterate increment 0))

; filter == predicate + list
(filter (fn [name]
          (string/ends-with? name "x"))
        names)

; favourite
(map (fn [name]
       (string/upper-case name))
     names)

; indexed map
(map (fn [name idx]
       (str (string/upper-case name) "-" idx))
     names
     (range (count names)))                                 ; adds index

; filter + map combined!
(keep (fn [name]
        (when (string/ends-with? name "x")                  ;nil response gets taken out of return
          (string/upper-case name)))
      names)

; like keep but only returns the first match
(some (fn [name]
        (when (string/ends-with? name "x")                  ;nil response gets taken out of return
          (string/upper-case name)))
      names)

; applies map and concat but also works like keep, map then flatten basically
(mapcat (fn [name]
          ; return a list of two strings
          [(string/upper-case name)
           (string/reverse name)])
        names)

; group-by not lazy
(group-by (fn [name]
            (subs name 0 1))
          names)

; no longer a map :(
(sort-by key (group-by (fn [name]
                         (subs name 0 1))
                       names))

; magic
(into (sorted-map) (group-by (fn [name]
                               (subs name 0 1))
                             names))

; reduce my old friend
(reduce (fn [acc curr]
          (str acc "-" curr))
        names)

; reduce with init value
(reduce (fn [acc curr]
          (str acc "-" curr))
        "firstArgInit"
        names)

; reduce but return all intermediate values :o - actually amazing!
(reductions (fn [acc curr]
              (str acc "-" curr))
            "firstArgInit"
            names)

; lets implement filter for the lolz
(defn my-filter [test-fn coll]
  (reduce (fn [acc curr]
            (if (test-fn curr)
              (conj acc curr)
              acc))
          (empty coll)
          coll))

; eehhhhh (but this version creates a vector)
(my-filter (fn [name]
             (string/ends-with? name "x"))
           names)

; lets do map for fun too
(defn my-map [trans-fn coll]
  (reduce (fn [acc curr]
            (conj acc (trans-fn curr)))
          (empty coll)
          coll))

(my-map (fn [name]
          (string/upper-case name))
        names)

; sum time
(defn my-some [transform-fn coll]
  (reduce (fn [result element]
            (if (nil? result)                               ; if the current result is not found yet do ...
              (let [transformed-el (transform-fn element)]  ; transform it
                (if (nil? element)                          ; is still nil?
                  result                                    ; then keep iterating
                  transformed-el))                          ; otherwise return the transformed el
              result))
          nil
          coll))

(defn my-better-some [transform-fn coll]
  (reduce (fn [result element]
            (let [transformed-el (transform-fn element)]
              (if (nil? element)
                result
                (reduced transformed-el))))                 ; this is magic, it stops reduce iteration once this is returned!
          nil
          coll))

(my-better-some (fn [name]
                  (when (string/ends-with? name "x")        ;nil response gets taken out of return
                    (string/upper-case name)))
                names)
; basic
(reduce (fn [sum n]
          (+ sum n))
        0
        [1 2 3 4])

; method ref (inlining)
(reduce + [1 2 3 4])
(reduce * 4 [1 2 3 4])
(reduce str names)
(map string/upper-case names)
; etc etc

; returns an add (+) function with 1 2 3 added to wtv you add in addition (currying with multiple args?)
(def increase (partial + 1 2 3))
(increase 1 2 10)

; remember the above
; partial returns an equals func with alex already applied
; which is the same as passing an anonymous func ... truly amazing
(filter (partial = "alex") names)

; short form anon func :o (all eq)
(filter #(= "alex" %) names)
(filter #(= "alex" %1) names)
(filter #(= % "alex") names)

; one-liners are cool for these short calls
(filter #(string/ends-with? % "x") names)

; real-world from defold (fuzzy-search) ... basic form
(defn filter-options-basic [option->text options filter-text]
  (if (empty? filter-text)
    options
    (map :option (sort-by :score (keep (fn [option]
                                         (when-some [score (string/index-of (option->text option) filter-text)]
                                           {:option option :score score}))
                                       options)))))

; equivalent but using "threading operator" + make score func
(defn decorate-with-score [filter-text option->text option]
  (when-some [score (string/index-of (option->text option) filter-text)]
    {:option option :score score}))

(defn filter-options [option->text options filter-text]
  (if (empty? filter-text)
    options
    (->> options
         (keep (partial decorate-with-score filter-text option->text))
         (sort-by :score)
         (map :option))))

(filter-options-basic string/lower-case  names "e")
(filter-options string/lower-case  names "e")