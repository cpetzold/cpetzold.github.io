(ns blog.core
  (:use plumbing.core)
  (:require
   [clojure.string :as str]
   [me.raynes.fs :as fs]
   [hiccup.def :refer [defhtml]]
   [hiccup.page :as page]
   [hiccup.core :as hiccup]
   hiccup.compiler
   [garden.core :as garden]
   [endophile.core :refer [mp]]
   [endophile.hiccup :refer [to-hiccup]]
   [blog.css :as css]))

(def +meta-regexp+ #"^\s*([\w-]+:\s*[\w-\s]+\n)*[\w-]+:\s*[\w-]+")

(defn replace-first [s n match replacement]
  (loop [i 0
         s s]
    (if (= i n)
      s
      (recur (inc i) (str/replace-first s match replacement)))))

(defn base-path [path]
  (->> (str/split path #"/")
       butlast
       (str/join "/")))

(defhtml page-html [title & body]
  (page/html5
   [:head
    [:title title]
    (page/include-css
     "http://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600,700")
    (page/include-js
     "http://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.1/highlight.min.js"
     "http://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.1/languages/clojure.min.js")
    [:style (garden/css css/styles)]]
   [:body
    [:div#container body]
    [:script "hljs.initHighlightingOnLoad();"]]))

(defhtml index-html [posts]
  [:ol
   (for [post posts]
     (letk [[path [:metadata title date]] post]
       [:li
        [:time {:datetime date} date]
        [:a {:href path} title]]))])

(defhtml post-html [post]
  (letk [[html [:metadata title date]] post]
    [:article
     [:h1 title]
     [:time {:datetime date} date]
     html]))

(defn file-name->path [file-name]
  (assert
   (re-find #"^\d{4}-\d{2}-\d{2}-.*$" file-name)
   "Post filename must be in the format YYYY-MM-DD-*")
  (replace-first file-name 3 #"-" "/"))

(defn metadata [s]
  (for-map [line (-> (re-find +meta-regexp+ s) first (str/split #"\n"))
            :let [[key val] (map str/trim (str/split line #":"))]]
    (keyword (str/lower-case key)) val))

(defn strip-metadata [s]
  (-> s
      (str/replace +meta-regexp+ "")
      (str/replace #"^\s*" "")))

(defn file->post [file]
  (let [file-name (fs/name file)
        file-str (slurp file)
        md (strip-metadata file-str)]
    {:file-name file-name
     :path (-> file-name file-name->path fs/base-name)
     :md md
     :metadata (metadata file-str)
     :html (-> (mp md)
               to-hiccup
               hiccup/html)}))

(defn md-files [path]
  (fs/find-files path #".*\.(md|markdown)$"))

(defn posts [path]
  (map file->post (md-files path)))

(defn write-page! [path title body]
  (spit path (page-html title body)))

(defnk write-post! [path [:metadata title] :as post]
  (fs/mkdirs (base-path path))
  (write-page! (str path ".html") title (post-html post)))

(defn write-posts! [posts]
  (doseq [p posts]
    (write-post! p)))

(defn write-index! [posts]
  (write-page! "index.html" "Conner Petzold" (index-html posts)))

(defn write-blog! [posts]
  (doto posts
    write-index!
    write-posts!)
  nil)

(comment

  (->> (md-files "posts")
       (map file->post)
       write-blog!)

  )
