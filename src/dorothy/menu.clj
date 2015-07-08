(ns dorothy.menu
  (:import (java.awt MenuItem Menu)
           (java.awt.event ActionListener)))


(defn label
  "Creates a label for a menu."
  [text]
  (MenuItem. text))

(defn divider
  "Creates a divider for a menu."
  []
  (label "-"))

(defn button
  "Creates a clickable label for a menu. When clicked, the supplied callback is run."
  [text callback]
  (doto (label text)
    (.addActionListener
      (reify ActionListener
        (actionPerformed [this action]
          (callback))))))

(defn submenu
  "Creates a labelled menu that can be nested in other menus."
  [text & menuitems]
  (let [parent (Menu. text)]
    (doseq [child menuitems]
      (.add parent child))
    parent))
