(ns dorothy.menu
  (:import (java.awt MenuItem Menu)
           (java.awt.event ActionListener)))


(defn divider [] (MenuItem. "-"))

(defn label [text] (MenuItem. text))

(defn button [text callback]
  (doto (label text)
    (.addActionListener
      (reify ActionListener
        (actionPerformed [this action]
          (callback))))))

(defn submenu [text & menuitems]
  (let [parent (Menu. text)]
    (doseq [child menuitems]
      (.add parent child))
    parent))
