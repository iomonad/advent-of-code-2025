(defproject aoc2025 "0.1.0-SNAPSHOT"
  :description "AOC2025 - Clojure and Clerk"
  :url "https://github.com/iomonad/advent-of-code-2025"
  :dependencies [[org.clojure/clojure         "1.12.3"]
                 [io.github.nextjournal/clerk "0.18.1158"]
                 [com.cnuernber/charred       "1.037"]
                 [criterium                   "0.4.6"]]
  :source-paths ["dev" "notebooks" "src"]
  :aliases {"build-static" ["run" "-m" "build"]}
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "1.5.0"]]}
             :repl-options {:init-ns user}})
