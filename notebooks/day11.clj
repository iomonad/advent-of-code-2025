^{:nextjournal.clerk/visibility {:code :hide}}
(ns day11
  {:title "Day 11: Reactor"
   :description "Day 11: Reactor"
   :path "notebooks/day11"
   :preview "https://plus.unsplash.com/premium_photo-1741647116659-16d14245c76b?w=900&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NXx8dG9rYW1ha3xlbnwwfHwwfHx8MA%3D%3D"}
  (:require [nextjournal.clerk :as clerk]
            [utils :refer [get-aoc-input]]
            [clojure.string :as str]))

;;; # Day 11: Reactor

;;; You hear some loud beeping coming from a hatch in the floor of the factory, so you decide to check it out. Inside, you find several large electrical conduits and a ladder.

;;; Climbing down the ladder, you discover the source of the beeping: a large, toroidal reactor which powers the factory above. Some Elves here are hurriedly running between the reactor and a nearby server rack, apparently trying to fix something.

;;; One of the Elves notices you and rushes over. "It's a good thing you're here! We just installed a new server rack, but we aren't having any luck getting the reactor to communicate with it!" You glance around the room and see a tangle of cables and devices running from the server rack to the reactor. She rushes off, returning a moment later with a list of the devices and their outputs (your puzzle input).

;;; ## Input

;;; > Each line gives the name of a device followed by a list of the devices to which its outputs are attached. So, bbb: ddd eee means that device bbb has two outputs, one leading to device ddd and the other leading to device eee.

;;; > The Elves are pretty sure that the issue isn't due to any specific device, but rather that the issue is triggered by data following some specific path through the devices. Data only ever flows from a device through its outputs; it can't flow backwards.

;;; I wanted to give a try a real world solving with loop, this sounds to be the fit:

(def input (get-aoc-input))

;;; Transform the flat graph to data

(def graph-raw
  (into #{}
        (mapcat (fn [line]
                  (let [[node & [links]] (str/split line #":")]
                    (for [l (str/split (str/trim links) #" ")]
                      [node l])))
                input)))

^{::clerk/visibility {:result :hide}}
(require '[loom.graph :as loom])

;;; Build an unweighted, directed graph

(def digraph (apply loom/digraph graph-raw))

;;; We can also visualize the graph as follow

^{::clerk/visibility {:result :hide}}
(require '[loom.io :as lio])

(comment
  (lio/view digraph))

;;; Implement our implementation of DFS

;;; Find all the paths from start to end in a `digraph` and eturns a sequence of paths, where each path is a sequence of nodes

;;; https://github.com/aysylu/loom/blob/1.0.2/src/loom/graph.cljc#L72

^{::clerk/visibility {:result :hide}}
(defn loom-dfs
  [g start end]
  (when (loom/graph? g)
    (letfn
     [(dfs* [current path visited]
        (cond
          (= current end) [(conj path current)]
          (visited current) []       ; visited?
          :else                      ; Search
          (let [nv (conj visited current)
                np (conj path current)
                neighbors (loom/successors g current)]
            (->> neighbors
                 (mapcat #(dfs* % np nv))
                 (seq)))))]
      (trampoline dfs* start [] #{}))))

;;; Solve by counting all the paths

(def all-paths (loom-dfs digraph "you" "out"))

;;; Then counting all the path

(def part1 (count all-paths))

;;; ## Part2

;;; Thanks in part to your analysis, the Elves have figured out a little bit about the issue. They now know that the problematic data path passes through both dac (a digital-to-analog converter) and fft (a device which performs a fast Fourier transform).

;;; They're still not sure which specific path is the problem, and so they now need you to find every path from svr (the server rack) to out. However, the paths you find must all also visit both dac and fft (in any order).

(def vanilla-graph
  (->> input
       (map #(str/replace % ":" ""))
       (map #(str/split % #" "))
       (reduce (fn [gacc tokens]
                 (assoc gacc (first tokens) (rest tokens)))
               {})))

;;; Memoized recursive check function

^{::clerk/visibility {:result :hide}}
(def dfs-vanilla-memo*
  (memoize
   (fn [g begin end]
     (if (= begin end) 1  (apply + (map #(dfs-vanilla-memo* g % end) (g begin)))))))

^{::clerk/visibility {:result :hide}}
(defn dfs-count-vanilla
  [g begin end]
  (dfs-vanilla-memo* g begin end))

;;; Compare vanilla implementation and loom one

(=
 (count (loom-dfs digraph "you" "out"))
 (dfs-count-vanilla vanilla-graph "you" "out"))

;;; Compute number of path between dac and fft

(def dac->fft (dfs-count-vanilla vanilla-graph "dac" "fft"))

;;; Compute number of path between fft and dac

(def fft->dac (dfs-count-vanilla vanilla-graph "fft" "dac"))

;;; Compute number of path between server and fft

(def srv->fft (dfs-count-vanilla vanilla-graph "svr" "fft"))

;;; Compute number of possible

(def dac->out (dfs-count-vanilla vanilla-graph "dac" "out"))

;;; Since we don't have directed link from dac to fft, we can ommit the path and fast-forward resolution as follow

;;; `SRV->FFT->DAC->OUT`

;;; Were all the commutation is modelized as [SRV->FFT] * [FFT->DAC] * [DAC->OUT]

(def part2 (* srv->fft fft->dac dac->out))
