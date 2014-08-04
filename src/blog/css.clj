(ns blog.css
  (:refer-clojure :exclude [rem])
  (:require
   [clojure.string :as str]
   [garden.def :refer [defstyles defcssfn defkeyframes]]
   [garden.stylesheet :refer [cssfn]]
   [garden.units :refer [px percent em rem vw vh vmax deg]]))

(defcssfn url)

(defcssfn translateY)

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

(defkeyframes float-animation
  [:from
   ^:prefix {:transform (translateY 0)}]

  [:to
   ^:prefix {:transform (translateY (rem -0.4))}])

(defstyles styles
  float-animation

  (at-font-face
   "DejaVu Sans Mono"
   "/fonts/DejaVuSansMono-webfont"
   [:woff :ttf])

  [:*
   {:box-sizing "border-box"}]

  [:html
   {:font {:size (vmax 1.6)}}]

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
   ^:prefix {:animation [[float-animation "4s" :infinite :alternate]]}
   {:position "fixed"
    :z-index -1
    :width (rem 7)
    :height (rem 7)
    :top (vh 20)
    :left (vw 65)
    :background {:image "url(images/conner.png)"
                 :size "contain"
                 :repeat "no-repeat"}}]

  [:#laptop
   ^:prefix {:animation [[float-animation "4s" :infinite :alternate "2s"]]}
   {:position "fixed"
    :z-index -1
    :width (rem 7)
    :height (rem 7)
    :top (vh 14)
    :left (vw 58)
    :background {:image "url(images/laptop.png)"
                 :size "contain"
                 :repeat "no-repeat"}}]

  [:#content
   {:background :#fff
    :padding [[(rem 2) 0]]}]

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
