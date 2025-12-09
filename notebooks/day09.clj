^{:nextjournal.clerk/visibility {:code :hide}}
(ns day09
  {:title "Day 9: Movie Theater"
   :description "Day 9: Movie Theater"
   :path "notebooks/day09"
   :preview "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8bW92aWUlMjB0aGVhdGVyfGVufDB8fDB8fHww"}
  (:require [nextjournal.clerk :as clerk]
            [utils :refer [get-aoc-input]]
            [clojure.math.combinatorics :as combo]
            [clojure.string :as str]))

;;; # Day 9: Movie Theater

;;; You slide down the firepole in the corner of the playground and land in the North Pole base movie theater!

;;; The movie theater has a big tile floor with an interesting pattern. Elves here are redecorating the theater by switching out some of the square tiles in the big grid they form. Some of the tiles are red; the Elves would like to find the largest rectangle that uses red tiles for two of its opposite corners. They even have a list of where the red tiles are located in the grid (your puzzle input).

;;; ## Input

(def input (get-aoc-input))

;;; Extract raw input as X,Y red chair grid

(def red-tiles-grid
  (->> input
       (mapv #(str/split % #","))
       (mapv (partial mapv read-string))))

;;; ## Part 1

;;; > Using two red tiles as opposite corners, what is the largest area of any rectangle you can make?

;;; Compute area

(defn compute-area [a b] (reduce * (map #(inc (abs (- %1 %2))) a b)))

;;; From generated computation, apply max

(defn solve [points] (reduce max (map #(apply compute-area %) (combo/combinations points 2))))

;;; Resolve from input

(def part1 (solve red-tiles-grid))
