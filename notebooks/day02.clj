^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns day02
  {:title "Day02"
   :description "Day 2: Gift Shop"
   :path "notebooks/day02"
   :preview "https://images.unsplash.com/photo-1603912699214-92627f304eb6?w=900&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Z2lmdCUyMHNob3B8ZW58MHx8MHx8fDA%3D"}
  (:require [clojure.java.io :as io]
            [utils :refer [get-aoc-input]]
            [clojure.string :as str]))

;;; # Day 2: Gift Shop

;;; As you make your way through the surprisingly extensive selection, one of the clerks recognizes you and asks for your help.

;;; # Part 1

;;; ## Input

;;; They've even checked most of the product ID ranges already; they only have a few product ID ranges (your puzzle input) that you'll need to check.

;;; *The ID ranges are wrapped here for legibility; in your input, they appear on a single long line.*

;;; The ranges are separated by commas (,); each range gives its first ID and last ID separated by a dash (-).

;;; ### Input Validation

;;; Since the young Elf was just doing silly patterns, you can find the invalid IDs by looking for any ID which is made only of some sequence of digits repeated twice.

;;; So, 55 (5 twice), 6464 (64 twice), and 123123 (123 twice) would all be invalid IDs.

;;; Read raw AOC input as list of items
(def input (str/split (first (get-aoc-input)) #","))

;;; Slice the list of items by **first ID** and **last ID**
(def input-sliced
  (->> input
       (map (fn [raw] (str/split raw #"-")))
       (map (partial take 2))))

;;; Using Regex Backreferences with `re-matches`: https://docs.oracle.com/javase/tutorial/essential/regex/groups.html

;;; Could be also implemented with frequencies but I prefer this straightforward approach that remove migreana for some
;;; cases.

;;; We parse the range, generate the bounds then filter against the capture group regex.

^{:nextjournal.clerk/visibility {:result :hide}}
(defn invalid-id
  [[a b]]
  (let [r (range (read-string a) (inc (read-string b)))]
    (filter (fn [c]
              (re-matches #"^(\d+)\1$" (str c)))
            r)))

;;; What do you get if you add up all of the invalid IDs?

;;; Basically, we concat all the results, and reduce to the sum accumulator ...

(->> input-sliced
     (mapcat invalid-id)
     (reduce +))

;;; # Part 2
