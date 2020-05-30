(ns ecommerce.remembering)

(reduce + [1 2 3])
; => 6

;initial value
(reduce + 10 [1 2 3])
; => 16

(conj [1 2] 3)
;=> [1 2 3]

(conj [1 2] [3])
;=> [1 2 [3]]

(defn join [list element]
  (conj list [element]))

(reduce join [] [1 2 3])
; => [[1] [2] [3]]


(reduce (fn [list element] (conj list [element])) [] [1 2 3])
; => [[1] [2] [3]]
