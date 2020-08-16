(ns learn-clojure.collections)

; Vector3
[1 2 3]

; Map
{:a 1 :b 2}
; calling map as function
({:a 1 :b 2} :a)
; same for vectors
(let [my-vec [1 2 3]]
  (my-vec 1))

; out-of-bounds
(let [my-vec [1 2 3]]
  (my-vec 6))
; nil (not of bounds)
(let [my-map {:a 1 :b 2}]
  (my-map 6))

; set (of items, not a map)
(let [my-set #{:a :b 2}]
  (my-set :a))
; nil for not existent key
(let [my-set #{:a :b 2}]
  (my-set :c))

; special cases
(let [my-vec (vector-of :double 1.2 3.0)]
  (my-vec 0))

; map by key (keyword is the function that takes a map to
; look itself up in) wild!
(let [my-map {:a 1 :b 2}]
  (:a my-map))

; get will return nil on oob or you can use default
; (works on all collections)
(get [1 2] 7 :hello)

; going ham
(let [my-map {:a 1 :b 2}
      my-func (fn [] :a)]
  ((my-func) my-map))

; everything is immutable
(let [vec1 [1 2]
      vec2 (assoc vec1 0 3)] ;assoc (change value returns a new vector
  vec1)

; assoc maps too
(let [my-map {:a 1 :b 2}]
  (assoc my-map
    :c 3
    :d 4
    :a 0))

(let [my-map {:a 1 :b 2}]
  (dissoc my-map
    :a))

; nil means empty and can be assoc'd ... very cool!
(assoc nil :a 1)
(assoc nil :a 1 :b 2)

; nested map lookup
(let [my-map {:a 1 :b {:b1 "nested" :b2 "stuff"}}]
  (get-in my-map [:b :b2]))

(let [my-map {:a 1 :b {:b1 "nested" :b2 "stuff"}}]
  (get-in my-map [:b :b3])) ; nil

; cool
(let [my-map {:a 1 :b {:b1 "nested" :b2 "stuff"}}]
  (assoc-in my-map [:b :b2] "newValue"))