(ns utils
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn get-aoc-input
  ([]
   (get-aoc-input (str *ns*)))
  ([f]
   (some-> (io/resource (str "input/" f))
           (slurp)
           (str/split-lines))))
