^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns day05
  {:title "Day 5: Cafeteria"
   :description "Day 5: Cafeteria"
   :path "notebooks/day05"
   :preview "https://images.unsplash.com/photo-1675523300599-15f7a798a2cf?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"}
  (:require [nextjournal.clerk :as clerk]
            [utils :refer [get-aoc-input bench]]
            [clojure.set :as set]
            [clojure.string :as str]))

;;; #  Day 5: Cafeteria

;;; ## Introduction

;;; The Elves in the kitchen explain the situation: because of their complicated new inventory management system, they can't figure out which of their ingredients are fresh and which are spoiled. When you ask how it works, they give you a copy of their database (your puzzle input).

;;; The database operates on ingredient IDs. It consists of a list of fresh ingredient ID ranges, a blank line, and a list of available ingredient IDs.

;;; The fresh ID ranges are inclusive: the range 3-5 means that ingredient IDs 3, 4, and 5 are all fresh. The ranges can also overlap; an ingredient ID is fresh if it is in any range.

;;; ## Part 1

;;; Retrieve the file input

(def input (get-aoc-input))

;;; Retrieve range collection until empty string

(def freshness-ranges (take-while seq input))

;;; Then also generate ingredient range

(def ingredients (mapv read-string (drop 1 (drop-while seq input))))

;;; Generate freshness data, splitted by range, and parsed as number

(def freshness
  (mapv #(mapv read-string (str/split % #"-"))
        freshness-ranges))

;;; ### Interval compaction

;; Interval Merging helper (https://www.geeksforgeeks.org/dsa/merging-intervals/)

^{:nextjournal.clerk/visibility {:result :hide}}
(defn compact-intervals
  [interval-seq]
  (->> (sort-by first interval-seq)
       (reduce
        (fn [acc [s e]]
          (if-let [[a b] (peek acc)]
            (if (<= s b)
              (conj (pop acc) [a (max b e)])
              (conj acc [s e]))
            (conj acc [s e])))
        [])))

;;; Generate a compact version of intervals

(def freshness-compacted (compact-intervals freshness))

;;; Predicate to check intervals

^{:nextjournal.clerk/visibility {:result :hide}}
(defn included?
  [rs id]
  (some
   (fn [[beg end]]
     (cond
       (> beg id)      false
       (<= beg id end) true
       :else           false))
   rs))

;;; Check list against compacted intervals, then sum results

(def result (count (filter (partial included? freshness-compacted) ingredients)))

;;; ### Part 2

;;; For each compacted interval, compute inclusive differences, then sum
;;; all the results into an accumulator.

(def result-bis
  (reduce (fn [acc [a b]] (+ acc (- (inc b) a))) 0 freshness-compacted))
