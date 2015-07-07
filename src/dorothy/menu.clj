(ns dorothy.menu
  (:import (java.awt MenuItem)))


(defn divider [] (MenuItem. "-"))

(defn label [text] (MenuItem. text))

