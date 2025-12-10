^{:nextjournal.clerk/visibility {:code :hide}}
(ns day10
  {:title "Day 10: Factory"
   :description "Day 10: Factory"
   :path "notebooks/day10"
   :preview "https://images.unsplash.com/photo-1516937941344-00b4e0337589?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"}
  (:require [nextjournal.clerk :as clerk]
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

;;;
;;; ## Input

;;; Machine are modelized with vector of size 3
;;; First is binary representation of light, second is wiring, and last is joltage requirement

(def machines
  (mapv
   (fn [machine]
     (let [[light & manual] (str/split machine #" ")
           wiring (butlast manual)
           joltage (last manual)]
       (let [lights (->> (str/replace light #"[\[\]]" "")
                         (mapv {\# 1 \. 0}))
             wiring (->> (mapv #(str/replace % #"[\(\)]" "") wiring)
                         (mapv #(str/split % #","))
                         (mapv (partial mapv read-string))
                         (mapv set))
             joltage (map read-string
                          (-> (str/replace joltage #"[\{\}]" " ")
                              (str/split #",")))]
         [lights wiring (count lights)])))
   input))

;;; ## Rules

;;; To start a machine, its indicator lights must match those shown in the diagram, where . means off and # means on. The machine has the number of indicator lights shown, but its indicator lights are all initially off.

;;; So, an indicator light diagram like [.##.] means that the machine has four indicator lights which are initially off and that the goal is to simultaneously configure the first light to be off, the second light to be on, the third to be on, and the fourth to be off.

;;; You can toggle the state of indicator lights by pushing any of the listed buttons. Each button lists which indicator lights it toggles, where 0 means the first light, 1 means the second light, and so on. When you push a button, each listed indicator light either turns on (if it was off) or turns off (if it was on). You have to push each button an integer number of times; there's no such thing as "0.5 presses" (nor can you push a button a negative number of times).

;;; So, a button wiring schematic like (0,3,4) means that each time you push that button, the first, fourth, and fifth indicator lights would all toggle between on and off. If the indicator lights were [#.....], pushing the button would change them to be [...##.] instead.

;;; Because none of the machines are running, the joltage requirements are irrelevant and can be safely ignored.

;;;  ## Math fundations

;;; State can be modelized in [Galois field GF(2)](https://www.wolframalpha.com/input?i=GF%282%29)
;;; With GF(2):
;;;  - Addition become XOR operation
;;;  - Multiply become AND operation

;;; We don't care about twice toggle because state reset each cycles

;;; ## Solving

;;; First, generate a [Coefficient Matrix](https://en.wikipedia.org/wiki/Coefficient_matrix), a matrix consisting of the coefficients of the variables in a set of linear equations.

;;; The matrix is used in solving systems of linear equations.

;;; A where A[i][j] = 1 if button j toggles light i"

(defn build-coefficient-matrix
  [[_ wires lights-total]]
  (vec
   (for [l (range lights-total)]
     (vec
      (for [w wires]
        (if (w l) 1 0))))))

;;; Build [Augmented Matrix](https://en.wikipedia.org/wiki/Augmented_matrix)

(defn build-augmented-matrix
  [machine]
  (let [[lights] machine
        coefficient-matrix (build-coefficient-matrix machine)]
    (mapv conj coefficient-matrix lights)))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/caption
 "Gaussian Elimination to Obtain RREF"
 (clerk/html
  [:iframe {:width 500
            :height 315
            :src "https://www.youtube.com/embed/sqCTgDBEn7k"
            :title "Gaussian Elimination to Obtain RREF"
            :frameBorder 0
            :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
            :allowFullScreen true}]))

;;; Utils:
;;; - Calculator https://www.emathhelp.net/fr/calculators/linear-algebra/reduced-row-echelon-form-rref-calculator/
;;; - Documentation https://fr.wikipedia.org/wiki/Matrice_%C3%A9chelonn%C3%A9e

;;; From Wikipedia, [Gaussian Elimination](https://en.wikipedia.org/wiki/Gaussian_elimination)
;;; > In mathematics, Gaussian elimination, also known as row reduction, is an algorithm for solving systems of linear equations. It consists of a sequence of row-wise operations performed on the corresponding matrix of coefficients. This method can also be used to compute the rank of a matrix, the determinant of a square matrix, and the inverse of an invertible matrix.

(defn find-pivot
  [matrix start-row col]
  (first
   (for [row (range start-row (count matrix))
         :when (= 1 (get-in matrix [row col]))]
     row)))

(defn swap-rows
  [matrix i j]
  (assoc matrix i (matrix j) j (matrix i)))

;;; Gaussian elimination barely stolen from https://gist.github.com/julianoks/e1eb4a349922c0e2c0a3c7495aad2f38

(defn xor-rows
  [row1 row2]
  (mapv bit-xor row1 row2))

(defn find-pivot
  [matrix start-row col]
  (first
   (for [row (range start-row (count matrix))
         :when (= 1 (get-in matrix [row col]))]
     row)))

(defn swap-rows
  [matrix i j]
  (assoc matrix i (matrix j) j (matrix i)))

(defn gaussian-elimination
  [matrix]
  (let [rows (count matrix)
        cols (count (first matrix))]
    (loop [m matrix
           current-row 0
           current-col 0]
      (if (or (>= current-row rows) (>= current-col (dec cols)))
        m
        (if-let [pivot-row (find-pivot m current-row current-col)]
          (let [m (if (not= pivot-row current-row)
                    (swap-rows m current-row pivot-row)
                    m)
                m (reduce
                   (fn [mat row]
                     (if (and (not= row current-row)
                              (= 1 (get-in mat [row current-col])))
                       (assoc mat row (xor-rows (mat row) (mat current-row)))
                       mat))
                   m
                   (range rows))]
            (recur m (inc current-row) (inc current-col)))
          (recur m current-row (inc current-col)))))))

;;; Matlab reference: https://fr.mathworks.com/help/matlab/ref/rref.html

;;; Wikipedia: [Row Echelon Fom](https://en.wikipedia.org/wiki/Row_echelon_form)

;;; Implement Solver

(defn solve
  [machine]
  (let [[_lights wires _lights-total] machine
        rref (gaussian-elimination (build-augmented-matrix machine))
        wires-total (count wires)]
    (if (some (fn [row]
                (and (= 1 (last row))
                     (every? zero? (take wires-total row))))
              rref)
      nil
      (let [solution (vec (repeat wires-total 0))]
        (reduce
         (fn [sol row]
           (let [ahead-idx
                 (first (keep-indexed
                         (fn [idx v] (when (= v 1) idx))
                         (take wires-total row)))]
             (if (and ahead-idx (< ahead-idx wires-total))
               (assoc sol ahead-idx (last row))
               sol)))
         solution
         rref)))))

;;; ## Part1

;;; > Analyze each machine's indicator light diagram and button wiring schematics. What is the fewest button presses required to correctly configure the indicator lights on all of the machines?

;;; ! NOT WORKING !

(some->> machines
         (mapcat solve)
         (reduce +))
