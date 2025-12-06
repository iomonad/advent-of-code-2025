^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns day06
  {:title "Day 6: Trash Compactor"
   :description "Day 6: Trash Compactor"
   :path "notebooks/day06"
   :preview "https://plus.unsplash.com/premium_photo-1744148531931-51d1a1c45590?q=80&w=484&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"}
  (:require  [nextjournal.clerk :as clerk]
             [utils :refer [get-aoc-input]]
             [clojure.string :as str]))

;;; # Day 6: Trash Compactor

;;; As you try to find a way out, you are approached by a family of cephalopods! They're pretty sure they can get the door open, but it will take some time. While you wait, they're curious if you can help the youngest cephalopod with her math homework.

;;; Cephalopod math doesn't look that different from normal math. The math worksheet (your puzzle input) consists of a list of problems; each problem has a group of numbers that need to be either added (+) or multiplied (*) together.

;;; ## Input

(def input (get-aoc-input))

;;; Split the line by spaces between (1..n) size

(def sanitized-input
  (mapv #(filter seq (str/split % #" ")) input))

;;; Parse the number into matrix

(def number-matrix
  (->> (butlast sanitized-input)
       (mapv (partial mapv read-string))))

;;; Keep operation section array, resolve function from symbol

(def operations
  (map (comp resolve read-string) (last sanitized-input)))

;;; Transpose the original matrix

(def number-matrix-transposed
  (apply mapv vector number-matrix))

;; ### Part 1

;;; Apply transposed matrix with operation, sum to acc

(def part1
  (->> (zipmap number-matrix-transposed operations)
       (reduce (fn [acc [nums op]]
                 (+ acc (apply op nums)))
               0)))
