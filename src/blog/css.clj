(ns blog.css
  (:refer-clojure :exclude [rem])
  (:require
   [garden.def :refer [defstyles]]
   [garden.units :refer [px rem]]))

(defstyles styles
  [:html
   {:font {:size (px 24)}}]

  [:body
   {:background :#fff
    :color :#222
    :font {:family ["Crimson Text" "serif"]}
    :line-height 1.6}]

  [:#container
   {:max-width (rem 32)
    :margin [[0 "auto"]]}])
