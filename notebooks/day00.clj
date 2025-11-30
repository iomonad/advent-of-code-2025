^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns day00
  (:require [clojure.java.io :as io]
            [utils :refer [get-aoc-input]]))

;;; Read the dataset

(def input (get-aoc-input))

(def parsed-input (map read-string input))
