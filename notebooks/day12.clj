^{:nextjournal.clerk/visibility {:code :hide}}
(ns day12
  {:title "Day 12: Christmas Tree Farm"
   :description "Day 12: Christmas Tree Farm"
   :path "notebooks/day12"
   :preview "https://images.unsplash.com/photo-1474879231237-389a64ac46cd?w=900&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTR8fHBpbmUlMjB0cmVlfGVufDB8fDB8fHww"}
  (:require [nextjournal.clerk :as clerk]
            [utils :refer [get-aoc-input]]
            [clojure.string :as str]))

;;; # Day 12: Christmas Tree Farm

;;; You're almost out of time, but there can't be much left to decorate. Although there are no stairs, elevators, escalators, tunnels, chutes, teleporters, firepoles, or conduits here that would take you deeper into the North Pole base, there is a ventilation duct. You jump in.

;;; After bumping around for a few minutes, you emerge into a large, well-lit cavern full of Christmas trees!

;;; There are a few Elves here frantically decorating before the deadline. They think they'll be able to finish most of the work, but the one thing they're worried about is the presents for all the young Elves that live here at the North Pole. It's an ancient tradition to put the presents under the trees, but the Elves are worried they won't fit.

;;; The presents come in a few standard but very weird shapes. The shapes and the regions into which they need to fit are all measured in standard units. To be aesthetically pleasing, the presents need to be placed into the regions in a way that follows a standardized two-dimensional unit grid; you also can't stack presents.

;;; As always, the Elves have a summary of the situation (**your puzzle input**) for you. First, it contains a list of the presents' shapes. Second, it contains the size of the region under each tree and a list of the number of presents of each shape that need to fit into that region.

;;; ## Input

(def input (get-aoc-input))

;;; Build situation summary

(def raw-summary (map reverse (split-with (comp some? seq) (reverse input))))

;;; Register shapes

(def shapes
  (->> (partition-by (complement seq) (second raw-summary))
       (filter (partial every? seq))
       (reduce (fn [acc x]
                 (let [[head & rest] x
                       pos (read-string (apply str (take-while #(not= % \:) head)))]
                   (assoc acc pos (mapv (partial mapv #(if (= % \#) 1 0)) rest))))
               {})))

;;; Register Regions

(def regions
  (reduce
   (fn [acc line]
     (let [[dim pos] (str/split line #":")
           dim (mapv read-string (str/split dim #"x"))
           qty (->> (str/split pos #" ")
                    (filter seq)
                    (mapv read-string))]
       (assoc acc dim qty)))
   {}
   (first raw-summary)))

;;; ## Part1

;;; > Consider the regions beneath each tree and the presents the Elves would like to fit into each of them. How many of the regions can fit all of the presents listed?
