^{:nextjournal.clerk/visibility {:code :hide}}
(ns day10
  {:title "Day 10: Factory"
   :description "Day 10: Factory"
   :path "notebooks/day10"
   :preview "https://images.unsplash.com/photo-1516937941344-00b4e0337589?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"}
  (:require [nextjournal.clerk :as clerk]
            [clojure.set :as set]
            [clojure.math.combinatorics :as combo]
            [utils :refer [get-aoc-input]]
            [clojure.string :as str]))

;;; # Day 10: Factory

;;; The Elves do have the manual for the machines, but the section detailing the initialization procedure was eaten by a Shiba Inu. All that remains of the manual are some indicator light diagrams, button wiring schematics, and joltage requirements for each machine.

;;; ## Domain

;;; - Indicator light => Binary State
;;; - Swith => swap binary state of set of light
;;; - Result => minimum number of switch press

;;; The day looks like a [Lights Out](https://mathworld.wolfram.com/LightsOutPuzzle.html)

(def input (get-aoc-input))

;;; ## Input parsing

;;; The indicator lights = set where state == ON
;;; Button = togglable set
;;; Joltage, list

^{::clerk/visibility {:result :hide}}
(defn parse-machine
  [line]
  (let [[raw-indicators & more] (map #(subs % 1 (dec (count %))) (str/split line #"\s+"))
        raw-joltages (last more)
        raw-buttons (butlast more)
        indicators (->> (map-indexed vector raw-indicators)
                        (filter (fn [[_ ch]] (= ch \#)))
                        (map first)
                        set)
        buttons (mapv (fn [raw-btn]
                        (->> (str/split raw-btn #",")
                             (map #(Integer/parseInt %))
                             set))
                      raw-buttons)
        joltages (->> (str/split raw-joltages #",")
                      (map #(Integer/parseInt %))
                      vec)]
    [indicators buttons joltages]))

(def machines (mapv parse-machine input))

;;; ## Rules

;;; To start a machine, its indicator lights must match those shown in the diagram, where . means off and # means on. The machine has the number of indicator lights shown, but its indicator lights are all initially off.

;;; So, an indicator light diagram like [.##.] means that the machine has four indicator lights which are initially off and that the goal is to simultaneously configure the first light to be off, the second light to be on, the third to be on, and the fourth to be off.

;;; You can toggle the state of indicator lights by pushing any of the listed buttons. Each button lists which indicator lights it toggles, where 0 means the first light, 1 means the second light, and so on. When you push a button, each listed indicator light either turns on (if it was off) or turns off (if it was on). You have to push each button an integer number of times; there's no such thing as "0.5 presses" (nor can you push a button a negative number of times).

;;; So, a button wiring schematic like (0,3,4) means that each time you push that button, the first, fourth, and fifth indicator lights would all toggle between on and off. If the indicator lights were [#.....], pushing the button would change them to be [...##.] instead.

;;; Because none of the machines are running, the joltage requirements are irrelevant and can be safely ignored.

;;; ## Part 1

;;; First, test all possible button commutations of all possible lengths

^{::clerk/visibility {:result :hide}}
(defn test-lights
  [[indicators buttons _joltage]]
  (let [n (count buttons)]
    (loop [click 0]
      (when (<= click n)
        (if-let
         [found
          (some (fn [presses]
                  (let [commut (reduce
                                (fn [acc btn]
                                  (set/union
                                   (set/difference acc btn)
                                   (set/difference btn acc)))
                                #{}
                                presses)]
                    (when (= commut indicators)
                      click)))
                (combo/combinations buttons click))]
          found
          (recur (inc click)))))))

;;; Solve bruteforce, sum results

(def part1
  (->> machines
       (map test-lights)
       (reduce +)))
