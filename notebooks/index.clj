^{:nextjournal.clerk/visibility {:code :hide}}
(ns index
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [nextjournal.clerk :as clerk])
  (:import javax.imageio.ImageIO
           java.net.URL))

;;; ## Introduction
;;; [Advent of Code](https://adventofcode.com/2025) is an Advent calendar of small programming puzzles for a variety of skill levels that can be solved in any programming language you like. People use them as interview prep, company training, university coursework, practice problems, a speed contest, or to challenge each other.

;;; You don't need a computer science background to participate - just a little programming knowledge and some problem solving skills will get you pretty far. Nor do you need a fancy computer; every problem has a solution that completes in at most 15 seconds on ten-year-old hardware.

;;; ## Tools
;;; For this edition, Clojure and [Clerk](https://clerk.vision) will be used.

;;; ## Assignements

^{:nextjournal.clerk/visibility {:code :hide}}

;;; courtesy of `clerk-demo` repo
(let [aoc-notebooks
      [{:title "Day00"
        :preview "https://images.unsplash.com/photo-1606237598791-e862e965fb9d?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        :path "notebooks/day00"
        :description "Day00"}]]
  (clerk/html
   (into
    [:div.md:grid.md:gap-8.md:grid-cols-2.pb-8]
    (map
     (fn [{:keys [path preview title description]}]
       [:a.rounded-lg.shadow-lg.border.border-gray-300.relative.flex.flex-col.hover:border-indigo-600.group.mb-8.md:mb-0
        {:href (clerk/doc-url path) :title path :style {:height 300}}
        [:div.flex-auto.overflow-hidden.rounded-t-md.flex.items-center.px-3.py-4
         [:img {:src preview :width "100%" :style {:object-fit "contain"}}]]
        [:div.sans-serif.border-t.border-gray-300.px-4.py-2.group-hover:border-indigo-600
         [:div.font-bold.block.group-hover:text-indigo-600 title]
         [:div.text-xs.text-gray-500.group-hover:text-indigo-600.leading-normal description]]])
     aoc-notebooks))))

;;; -----
^{:nextjournal.clerk/visibility {:code :hide}}
(ImageIO/read (URL. "https://images.unsplash.com/photo-1528659432556-884cfe1480ef?w=700&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Z25vbWVzfGVufDB8fDB8fHww"))
