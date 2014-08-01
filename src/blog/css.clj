(ns blog.css
  (:refer-clojure :exclude [rem])
  (:require
   [garden.def :refer [defstyles defcssfn]]
   [garden.stylesheet :refer [cssfn]]
   [garden.units :refer [px em rem]]))

(defcssfn url)

(def extension->format
  {:otf "opentype"
   :ttf "truetype"
   :woff "woff"})

(defn font-src [src extension]
  [(url (format "%s.%s" src extension))
   ((cssfn :format) (format "'%s'" (extension->format extension)))])

(defn at-font-face [family src extensions]
  ["@font-face"
   {:font {:family family}}
   {:src (mapv (partial font-src src) extensions)}])

(defstyles styles
  (at-font-face
   "DejaVu Sans Mono"
   "/fonts/DejaVuSansMono-webfont"
   [:woff :ttf])

  [:html
   {:font {:size (px 18)}}]

  [:body
   {:background :#fff
    :color :#222
    :font {:family ["Source Sans Pro" "sans-serif"]}
    :line-height 1.8}]

  [:#container
   {:max-width (rem 36)
    :margin [[0 "auto"]]}]

  [:pre
   {:background :#202026}]

  [:code
   {:display "block"
    :overflow {:x "auto"}
    :font {:family ["DejaVu Sans Mono" "monospace"]
           :size (rem 0.7)}
    :padding (em 0.7)
    :color :#bfbfbf}]

  [:.hljs-built_in
   {:font {:weight "bold"}
    :color :#fff}]

  [:.hljs-attribute
   {:color :#89BDFF}]

  [:.hljs-string
   {:color :#43bfbf}
   ])
