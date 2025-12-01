^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns day01
  {:title "Day01"
   :description "Day 1: Secret Entrance"
   :path "notebooks/day01"
   :preview "https://images.unsplash.com/photo-1553293377-e51189852c0b?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTMyfHxzYWZlJTIwbG9ja3xlbnwwfHwwfHx8MA%3D%3D"}
  (:require [clojure.java.io :as io]
            [utils :refer [get-aoc-input]]
            [clojure.string :as str]))

;;; # Day 1: Secret Entrance
;;; ## Problem Context
;;; The safe has a dial with only an arrow on it; around the dial are the numbers 0 through 99 in order.

;;; As you turn the dial, it makes a small click noise as it reaches each number.

;;; The actual password is the number of times the dial is left pointing at 0 after any rotation in the sequence.
;;; ## Reading the input

(def input (get-aoc-input))
;;; > The attached document (your puzzle input) contains **a sequence of rotations**, one per line, which tell you how to open the safe.

;;; > A rotation starts with an **l** or **r** which indicates whether the rotation should be to the left (toward lower numbers) or to the right (toward higher numbers).

;;; > Then, the rotation has a distance value which indicates how **many clicks** the dial should be rotated in that direction.

;;; # Part 1
;;; ## Solving
;;; The first iteration would be to parse the input as command stack
(def command-stack
  (mapv (fn [raw-op]
          {:op     (condp = (keyword (str (first raw-op)))
                     :L -
                     :R +)
           :offset (read-string (apply str (rest raw-op)))})
        input))

;;; Then **reduce** the command stack into an integer state:

;; The dial starts by pointing at 50.
(defonce default-dial-position 50)

;;; The core logic of this safe cracking challenge, managing bounds projection
;;; while clocking around.

;;; This can be modelized using modular arithmetic over 0–99.
(defn evaluate-bounds [dial {:keys [op offset]}]
  (mod (op dial offset) 100))

;;; By computing the frequencies over the evaluation history, we can retrieve
;;; the current password.
(def solution-one
  (-> (reduce (fn [[dial results] op]
                (let [dial' (evaluate-bounds dial op)]
                  [dial' (conj results dial')]))
              [default-dial-position []]
              command-stack)
      (last)
      (frequencies)
      (get 0)))

;;; # Part 2

;;; > You remember from the training seminar that "method 0x434C49434B" means you're actually supposed to count the number of times any click causes the dial to point at 0, regardless of whether it happens during a rotation or at the end of one.

;;; We need to keep track of pointer swap on zero.
;;; First low effort approach would be to work only with 1-n steppers:
(def command-stack-flatten
  (->> command-stack
       (mapcat (fn [{:keys [op offset]}]
                 (take offset (repeatedly (constantly {:op op :offset 1})))))))

(count command-stack-flatten)

;;; **652177** should be sufficient for our hardware, we can now use it in
;;; the last solution implementation without any logic change.

(def solution-two
  (-> (reduce (fn [[dial results] op]
                (let [dial' (evaluate-bounds dial op)]
                  [dial' (conj results dial')]))
              [default-dial-position []]
              command-stack-flatten)
      (last)
      (frequencies)
      (get 0)))
