^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns day04
  {:title "Day 4: Printing Department"
   :description "Day 4: Printing Department"
   :path "notebooks/day04"
   :preview "https://images.unsplash.com/photo-1503694978374-8a2fa686963a?q=80&w=1738&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"}
  (:require [nextjournal.clerk :as clerk]
            [clojure.string :as str]
            [utils :refer [get-aoc-input]]))

;;; # Day 4: Printing Department

;;; ## Introduction

;;; You ride the escalator down to the printing department. They're clearly getting ready for Christmas; they have lots of large rolls of paper everywhere, and there's even a massive printer in the corner (to handle the really big print jobs).

;;; Decorating here will be easy: they can make their own decorations. What you really need is a way to get further into the North Pole base while the elevators are offline.

;;; "Actually, maybe we can help with that," one of the Elves replies when you ask for help. "We're pretty sure there's a cafeteria on the other side of the back wall. If we could break through the wall, you'd be able to keep moving. It's too bad all of our forklifts are so busy moving those big rolls of paper around."

;;; If you can optimize the work the forklifts are doing, maybe they would have time to spare to break through the wall.

;;; ## Input

;;; The rolls of paper (@) are arranged on a large grid; the Elves even have a helpful diagram (your puzzle input) indicating where everything is located.

;;; Recap:

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/row
 (clerk/html [:span
              [:b "@"]
              "  = Roll of paper"])
 (clerk/html [:span
              [:b "x"]
              "  = Forklifts access"]))

(def input (get-aoc-input))

;;; From the input:

;;; Index rolls as [Set off coordinates](https://en.wikipedia.org/wiki/Cartesian_coordinate_system), we keep only rolls positions.

(def graph-input
  (->> (map-indexed
        (fn [y r]
          (keep-indexed
           (fn [x c]
             (when (= c \@) [x y]))
           r)) input)
       (apply concat)
       (set)))

;;; Compute Moore-Neighbor algo, see: https://en.wikipedia.org/wiki/Moore_neighborhood, central excluded.

;;; > See also https://en.wikipedia.org/wiki/Neighbourhood_(graph_theory)

;;; Some Conways's game of life flavor graph

^{:nextjournal.clerk/visibility {:result :hide}}
(def proj [-1 0 1])
^{:nextjournal.clerk/visibility {:result :hide}}
(defn compute-moore-neighborhood
  [[x y]]
  (->> (mapcat (fn [dx] (map (fn [dy] [dx dy]) proj)) proj)
       (remove #(= % [0 0]))
       (map (fn [[dx dy]] [(+ x dx) (+ y dy)]))))

;;; Compute at the position the 8 moor neighborhood at graph position, filter out rolls and sum, then binary compute the limit (for the filter)

^{:nextjournal.clerk/visibility {:result :hide}}
(defn can-forklifts?
  ([pos rolls]
   (can-forklifts? pos rolls 1))
  ([pos rolls rolls-total]
   (let [result (count (filter rolls (compute-moore-neighborhood pos)))]
     (> rolls-total result))))

;;; ## Part1

;;; The forklifts can only access a roll of paper if there are fewer than four rolls of paper in the eight adjacent positions. If you can figure out which rolls of paper the forklifts can access, they'll spend less time looking and more time breaking down the wall to the cafeteria.

;;; > Consider your complete diagram of the paper roll locations. How many rolls of paper can be accessed by a forklift?

(def result (count (filter #(can-forklifts? % graph-input 4) graph-input)))
