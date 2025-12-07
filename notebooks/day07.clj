^{:nextjournal.clerk/visibility {:code :hide}}
(ns day07
  {:title "Day 7: Laboratories"
   :description "Day 7: Laboratories"
   :path "notebooks/day07"
   :preview "https://images.unsplash.com/photo-1614308456595-a59d48697ea8?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"}
  (:require [nextjournal.clerk :as clerk]
            [utils :refer [get-aoc-input]]
            [clojure.set :as set]
            [clojure.string :as str]))

;;; ### Day 7: Laboratories

;; You thank the cephalopods for the help and exit the trash compactor, finding yourself in the familiar halls of a North Pole research wing.

;; Based on the large sign that says "teleporter hub", they seem to be researching teleportation; you can't help but try it for yourself and step onto the large yellow teleporter pad.

;; Suddenly, you find yourself in an unfamiliar room! The room has no doors; the only way out is the teleporter. Unfortunately, the teleporter seems to be leaking magic smoke.

;; Since this is a teleporter lab, there are lots of spare parts, manuals, and diagnostic equipment lying around. After connecting one of the diagnostic tools, it helpfully displays error code 0H-N0, which apparently means that there's an issue with one of the tachyon manifolds.

;; You quickly locate a diagram of the tachyon manifold (your puzzle input). A tachyon beam enters the manifold at the location marked S; tachyon beams always move downward. Tachyon beams pass freely through empty space (.). However, if a tachyon beam encounters a splitter (^), the beam is stopped; instead, a new tachyon beam continues from the immediate left and from the immediate right of the splitter.

;;; ## Parsing input

(def input (get-aoc-input))

;;; Coerce char vec to binary vec

(def tychon-beam (mapv #(if (= % \S) 1 0) (first input)))

;;; Build manifold

(def manifold (drop 1 input))

;;; Build manifold representation by keeping only splitters

(defn get-splitters
  [line row]
  (filter
   (fn [cur]
     ;; RULE: SPLITTER and not already check
     (and (= (get line cur) \^)
          (not (= (get row cur) 0))))
   (range (count line))))

;;; Generate split row position values [n-1/n/n+1] for each
;;; split occurences

(defn compute-row
  [line row splitters]
  (reduce
   (fn [r pos]
     (-> r
         (assoc pos 0) ; checked, first cycle
         (update-in [(dec pos)] + (get r pos 0))
         (update-in [(inc pos)] + (get r pos 0))))
   row splitters))

;;; Tail rec the rows until last, return splits total. Evolved for part2 by
;;; keeping track of computed row, required for summing up

(defn compute-beam-splitter
  [row split-tot [line & rest-lines]]
  (if-not line
    [split-tot row]
    (let [splitters (get-splitters line row)]
      (recur (vec (compute-row line row splitters))
             (+ split-tot (count splitters))
             (vec rest-lines)))))

;;; Return the accumulator

(def part1 (first (compute-beam-splitter tychon-beam 0 manifold)))

;;; ## Part 2

;;; Apply the many-worlds interpretation of quantum tachyon splitting to your manifold diagram. In total, how many different timelines would a single tachyon particle end up on?

;;; After some refactoring of part1 implementation, we keep track of incremented possible path, his yield a graph interpreted as follow:

;;; Pseudo-reprentation:
;;```
;; 1-1-1-1-1-1..
;;    \1-1-1-1..
;;;       \2-2-..
;;;           \3
;;```

;;; At the end of the reduction, the tree accumulation represent all the path possible, we can simply reduce it

(def part2
  (reduce + (last (compute-beam-splitter tychon-beam 0 manifold))))
