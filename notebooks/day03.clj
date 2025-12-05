^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns day03
  {:title "Day 3: lobby"
   :description "Day 3: lobby"
   :path "notebooks/day03"
   :preview "https://images.unsplash.com/photo-1676569682207-d3bf4dfe483d?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=m3wxmja3fdb8mhxzzwfyy2h8mtb8fgxvymj5jtiwz25vbwv8zw58mhx8mhx8fda%3d"}
  (:require [utils :refer [get-aoc-input]]))

;;; # day 3: lobby

;;; there are batteries nearby that can supply emergency power to the escalator for just such an occasion.
;;; the batteries are each labeled with their joltage rating, a value from 1 to 9.

;;; you make a note of their joltage ratings (your puzzle input).

;;; ## input

(def input (get-aoc-input))

;;; convert each joltage ratings to a numerical collection:

(def numeric-input
  (mapv #(mapv (comp read-string str) %) input))

;;; Implementation using sliding sublist, slice by 10, and apply
;;; tail-rec accumulator. Could be implemented also with reduce

;; ```
;; ;; 12 0 [2 2 2 1 2 3 5 2 2 2 3 3 2 2 1 5 2 2 2 2 2 2 2 2 2 2 1 2 7 2 2 2 2 2 3 3 4 2 2 2 7 2 3 2 2 1 3 2 2 2 2 2 5 2 2 2 2 2 1 2 2 2 2 2 4 2 3 2 1 2 2 2 2 2 2 2 1 2 2 1 2 4 3 5 3 3 3 2 1 2 3 4 4 2 2 2 2 2 2 3]
;; ;; 11 7 (2 2 2 2 2 3 3 4 2 2 2 7 2 3 2 2 1 3 2 2 2 2 2 5 2 2 2 2 2 1 2 2 2 2 2 4 2 3 2 1 2 2 2 2 2 2 2 1 2 2 1 2 4 3 5 3 3 3 2 1 2 3 4 4 2 2 2 2 2 2 3)
;; ;; 10 77 (2 3 2 2 1 3 2 2 2 2 2 5 2 2 2 2 2 1 2 2 2 2 2 4 2 3 2 1 2 2 2 2 2 2 2 1 2 2 1 2 4 3 5 3 3 3 2 1 2 3 4 4 2 2 2 2 2 2 3)
;; ;; 9 775 (2 2 2 2 2 1 2 2 2 2 2 4 2 3 2 1 2 2 2 2 2 2 2 1 2 2 1 2 4 3 5 3 3 3 2 1 2 3 4 4 2 2 2 2 2 2 3)
;; ;; 8 7755 (3 3 3 2 1 2 3 4 4 2 2 2 2 2 2 3)
;; ;; 7 77554 (4 2 2 2 2 2 2 3)
;; ;; 6 775544 (2 2 2 2 2 2 3)
;; ;; 5 7755442 (2 2 2 2 2 3)
;; ;; 4 77554422 (2 2 2 2 3)
;; ;; 3 775544222 (2 2 2 3)
;; ;; 2 7755442222 (2 2 3)
;; ;; 1 77554422222 (2 3)
;;; ```

(defn compute-joltage
  [pos total bank]
  (if (zero? pos) total
      (let [a (apply max (drop-last (dec pos) bank))
            b (.indexOf bank a)]
        (println pos total bank)
        (recur (dec pos) (+ (* total 10) a) (drop (inc b) bank)))))

;; ## Part1:

;; Within each bank, you need to turn on exactly two batteries:

(->> numeric-input
     (map (partial compute-joltage 2 0))
     (reduce +))

;; ## Part2:

;; Now, you need to make the largest joltage by turning on exactly twelve batteries within each bank.

(->> numeric-input
     (map (partial compute-joltage 12 0))
     (reduce +))
