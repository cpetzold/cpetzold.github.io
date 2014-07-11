(ns blog.core
  (:use plumbing.core)
  (:require
   [clojure.string :as str]
   [me.raynes.fs :as fs]
   [hiccup.def :refer [defhtml]]
   [hiccup.page :as page]
   [hiccup.core :as hiccup]
   [garden.core :as garden]
   [markdown.core :as md]))

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

(defhtml page-html [body]
  (page/html5
   [:body body]))

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

(defn metadata [md]
  (for-map [line (-> (re-find +meta-regexp+ md) first (str/split #"\n"))
            :let [[key val] (map str/trim (str/split line #":"))]]
    (keyword (str/lower-case key)) val))

(defn strip-metadata [md]
  (str/replace
   (str/replace md +meta-regexp+ "")
   #"^\s*" ""))

(defn file->post [file]
  (let [file-name (fs/name file)
        md (slurp file)]
    {:file-name file-name
     :path (file-name->path file-name)
     :md (strip-metadata md)
     :metadata (metadata md)
     :html (md/md-to-html-string md)}))

(defn md-files [path]
  (fs/find-files path #".*\.(md|markdown)$"))

(defn posts [path]
  (map file->post (md-files path)))

(defn write-page! [path body]
  (spit path (page-html body)))

(defn write-post! [post]
  (let [path (:path post)]
    (fs/mkdirs (base-path path))
    (write-page! (str path ".html") (post-html post))))

(defn write-posts! [posts]
  (doseq [p posts]
    (write-post! p)))

(defn write-index! [posts]
  (write-page! "index.html" (index-html posts)))

(defn write-blog! [posts]
  (doto posts
    write-index!
    write-posts!))

(comment

  (->> (md-files "posts")
       (map file->post)
       write-blog!)

  )
