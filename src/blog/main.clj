(ns blog.main
  (:require
   [clojure.tools.namespace.repl :refer [refresh]]
   [me.raynes.fs :as fs]
   [watchtower.core :as wt]
   [blog.core :as blog]))

(defn -main []
  (wt/watcher
   ["src/" "posts/"]
   (wt/rate 50)
   (wt/on-change
    (fn [files]
      (require 'blog.core :reload)
      (require 'blog.css :reload)
      (blog/write-blog! (blog/posts "posts"))))))
