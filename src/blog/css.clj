(ns blog.css
  (:refer-clojure :exclude [rem])
  (:require
   [clojure.string :as str]
   [garden.def :refer [defstyles defcssfn]]
   [garden.stylesheet :refer [cssfn]]
   [garden.units :refer [px percent em rem vh vmax deg]]))

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

(defn linear-gradient [& args]
  (let [arg-str (->> args (map name) (str/join ", "))]
    (for [prefix ["" "-webkit-" "-moz-" "-o-"]]
      {:background-image (format "%slinear-gradient(%s)"
                                 prefix arg-str)})))

(defstyles styles
  (at-font-face
   "DejaVu Sans Mono"
   "/fonts/DejaVuSansMono-webfont"
   [:woff :ttf])

  [:*
   {:box-sizing "border-box"}]

  [:html
   {:font {:size (vmax 1.5)}}]

  [:body
   (linear-gradient "top" :#18181B :#202026)
   {:color :#222
    :font {:family ["Source Sans Pro" "sans-serif"]}
    :line-height 1.8
    :margin 0}]

  [:#header
   {:background "transparent"
    :position "relative"
    :height (vh 50)}

   [:.bottom
    {:position "absolute"
     :bottom 0
     :left 0
     :right 0}]

   [:h1 :h2
    {:color :#fff}
    [:small
     {:opacity 0.8}]]]

  [:#footer
   {:height (vh 100)}]

  [:#stars
   {:position "fixed"
    :z-index -1
    :top 0
    :left 0
    :width (percent 100)
    :height (percent 100)}]

  [:.star
   {:stroke-width 0}]

  [:#conner
   {:position "absolute"
    :width (rem 4)
    :height (rem 8)
    :left (percent 75)
    :bottom (rem -2)
    :background {:image "url(images/conner.png)"
                 :size "contain"
                 :repeat "no-repeat"}}]

  [:#content
   {:background :#fff}]

  [:.container
   {:max-width (rem 36)
    :padding [[0 (rem 1)]]
    :margin [[0 "auto"]]}]

  [:h1 :h2 :h3 :h4
   {:margin 0}]

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
