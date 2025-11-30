(ns user
  (:require [nextjournal.clerk :as clerk]))

(comment
  (clerk/serve! {:watch-paths ["notebooks"]
                 :browse? true
                 :port 8080})

  (clerk/show! "notebooks/warmup.clj"))
