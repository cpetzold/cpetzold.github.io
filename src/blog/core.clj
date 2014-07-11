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

(defn replace-first [s n match replacement]
  (loop [i 0
         s s]
    (if (= i n)
      s
      (recur (inc i) (str/replace-first s match replacement)))))

(defn base-path [path]
  (str/replace path #"/[^/]+$" ""))

(defhtml page [body]
  (page/html5
   [:body
    [:h1 "conner.codes"]
    body]))

(defn file-name->path [file-name]
  (assert
   (re-find #"^\d{4}-\d{2}-\d{2}-.*$" file-name)
   "Post filename must be in the format YYYY-MM-DD-*")
  (replace-first file-name 3 #"-" "/"))

(defn file->post [file]
  (let [file-name (fs/name file)
        md (slurp file)]
    {:file-name file-name
     :path (file-name->path file-name)
     :md md
     :html (md/md-to-html-string md)}))

(defn md-files [path]
  (fs/find-files path #".*\.(md|markdown)$"))

(defn posts [path]
  (map file->post (md-files path)))

(defn write-post! [post]
  (let [path (:path post)]
    (fs/mkdirs (base-path path))
    (spit (str path ".html")
          (page (:html post)))))

(defn write-posts! [posts]
  (doseq [p posts]
    (write-post! p)))

(comment

  (->> (md-files "posts")
       (map file->post)
       write-posts!)



  )
