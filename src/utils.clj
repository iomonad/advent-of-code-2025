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

(defn bench [f & args]
  (let [start (System/nanoTime)
        result (apply f args)
        end (System/nanoTime)
        elapsed-ms (/ (- end start) 1e6)]
    {:result result
     :time-ms elapsed-ms}))
