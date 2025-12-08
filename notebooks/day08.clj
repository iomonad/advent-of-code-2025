^{:nextjournal.clerk/visibility {:code :hide}}
(ns day08
  {:title "Day 8: Playground"
   :description "Day 8: Playground"
   :path "notebooks/day08"
   :preview "https://images.unsplash.com/photo-1596997000103-e597b3ca50df?w=900&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8cGxheWdyb3VuZHxlbnwwfHwwfHx8MA%3D%3D"}
  (:require [nextjournal.clerk :as clerk]
            [utils :refer [get-aoc-input]]
            [clojure.string :as str]
            [clojure.math :as math]
            [clojure.set :as set]
            [clojure.math.combinatorics :as comb]))

;;; # Day 8: Playground

;;; Equipped with a new understanding of teleporter maintenance, you confidently step onto the repaired teleporter pad.

;;; You rematerialize on an unfamiliar teleporter pad and find yourself in a vast underground space which contains a giant playground!

;;; Across the playground, a group of Elves are working on setting up an ambitious Christmas decoration project. Through careful rigging, they have suspended a large number of small electrical junction boxes.

;;; Their plan is to connect the junction boxes with long strings of lights. Most of the junction boxes don't provide electricity; however, when two junction boxes are connected by a string of lights, electricity can pass between those two junction boxes.

;; > The Elves are trying to figure out which junction boxes to connect so that electricity can reach every junction box. They even have a list of all of the junction boxes' positions in 3D space (your puzzle input).

;;; This list describes the position of 20 junction boxes, one per line. Each position is given as X,Y,Z coordinates. So, the first junction box in the list is at X=162, Y=817, Z=812.

;; To save on string lights, the Elves would like to focus on connecting pairs of junction boxes that are as close together as possible according to straight-line distance. In this example, the two junction boxes which are closest together are 162,817,812 and 425,690,689.

;;; ## Input

;;; First, parse input as x, y, z coord map

^{:nextjournal.clerk/visibility {:result :hide}}

(def input-raw (get-aoc-input))

(def input
  (->> input-raw
       (map #(str/split % #","))
       (map (partial map read-string))
       (map (partial interleave [:x :y :z]))
       (map #(apply hash-map %))))

(def input-num
  (->> input-raw
       (map #(str/split % #","))
       (map (partial map read-string))))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/caption
 "Scatter-plot representation of input data."
 (clerk/plotly
  {:data [{:x (mapv :x input)
           :y (mapv :y input)
           :z (mapv :z input)
           :mode "markers"
           :type "scatter3d"
           :marker {:size 3
                    :opacity 0.8}}]
   :layout {:margin {:l 0 :r 0 :b 0 :t 0}}}))

;;; ### Example experimentation
^{:nextjournal.clerk/visibility {:code :hide}}
(def input-sample
  [[162 817 812]
   [57 618 57]
   [906 360 560]
   [592 479 940]
   [352 342 300]
   [466 668 158]
   [542 29 236]
   [431 825 988]
   [739 650 466]
   [52 470 668]
   [216 146 977]
   [819 987 18]
   [117 168 530]
   [805 96 715]
   [346 949 466]
   [970 615 88]
   [941 993 340]
   [862 61 35]
   [984 92 344]
   [425 690 689]])

(def point-sample
  (->> input-sample
       (map (partial interleave [:x :y :z]))
       (map #(apply hash-map %))))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/caption
 "Example 3D space rendering"
 (clerk/plotly
  {:data [{:x (mapv :x point-sample)
           :y (mapv :y point-sample)
           :z (mapv :z point-sample)
           :mode "markers"
           :type "scatter3d"
           :marker {:size 4
                    :opacity 0.8}}]
   :layout {:margin {:l 0 :r 0 :b 0 :t 0}}}))

;;; Cheap implementation of https://en.wikipedia.org/wiki/Euclidean_distance
;;;
;;; Compute distance line between two 3d markers.

^{:nextjournal.clerk/visibility {:result :hide}}
(defn- dist
  [[[x1 y1 z1] [x2 y2 z2]]]
  (let [xi (abs (- x2 x1))
        yi (abs (- y2 y1))
        zi (abs (- z2 z1))]
    (math/sqrt (+ (* xi xi)
                  (* yi yi)
                  (* zi zi)))))

;;; Create a map of all the unique ways of taking pairs of points different elements from input coord

^{:nextjournal.clerk/visibility {:result :hide}}
(defn- dist-map
  [pts]
  (list pts (into {} (map #(vector % (dist %)) (comb/combinations pts 2)))))

;;; Format the two point, find the circuit then points into the circuit

^{:nextjournal.clerk/visibility {:result :hide}}
(defn- link
  [p1 p2 graph]
  (let [a (some #(when (% p1) %) graph)
        b (some #(when (% p2) %) graph)]
    (if-not (= a b)
      (conj (disj graph a b) (set/union a b))
      graph)))

;;; Set of connected components and, given weigthed pairs of points, links the closest pairs and return graph

^{:nextjournal.clerk/visibility {:result :hide}}
(defn- solve
  [n [points pairs]]
  (let [sorted-pairs (take n (sort #(compare (val %1) (val %2)) pairs))]
    (reduce (fn [graph [[p1 p2]]]
              (link p1 p2 graph))
            (set (map #(set [%]) points))
            sorted-pairs)))

;;; Part 1

;;; > Your list contains many junction boxes; connect together the **1000** pairs of junction boxes which are closest together. Afterward, what do you get if you multiply together the sizes of the three largest circuits?

(def part1
  (->> (dist-map input-num)
       (solve 1000)
       (map count)
       (sort)
       (reverse)
       (take 3)
       (apply *)))

;;; Part 2

;;; > Continuing the above example, the first connection which causes all of the junction boxes to form a single circuit is between the junction boxes at 216,146,977 and 117,168,530. The Elves need to know how far those junction boxes are from the wall so they can pick the right extension cable; multiplying the X coordinates of those two junction boxes (216 and 117) produces 25272.

;;; > Continue connecting the closest unconnected pairs of junction boxes together until they're all in the same circuit. What do you get if you multiply together the X coordinates of the last two junction boxes you need to connect?

;;; Evolved the last solver with realizing the full closests

^{:nextjournal.clerk/visibility {:result :hide}}
(defn- solve-bis
  [[points pairs]]
  (let [sp (sort #(compare (val %1) (val %2)) pairs)]
    (reduce (fn [graph [[p1 p2]]]
              (let [graphb (link p1 p2 graph)]
                (if (= 1 (count graphb))
                  ;; early yield of result (on find) not idiomatic but
                  ;; don't have time to rework this
                  (reduced (* (first p1) (first p2)))
                  graphb)))
            (set (map #(set [%]) points))
            sp)))

(def part2 (solve-bis (dist-map input-num)))
