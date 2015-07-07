(ns dorothy.core
  (:require [dorothy.system :refer [system-tray]]
            [dorothy.image :refer [make-image empty-image]])
  (:import (java.awt TrayIcon Color PopupMenu)))


(defn paint
  "Repaint the dot with the given colour."
  [[icon] colour]
  (.setImage icon (make-image colour)))

(defn destroy
  "Remove the dot from the system tray."
  [[icon]]
  (.remove system-tray icon))

(defn set-menu
  "Set the menu on this dot."
  [[_ popupmenu] & menuitems]
  (.removeAll popupmenu)
  (doseq [menuitem menuitems]
    (.add popupmenu menuitem)))

(defn make-dot
  "Makes a dot and adds it to the system tray."
  [tooltip]
  (let [popupmenu (PopupMenu.)
        icon (TrayIcon. empty-image tooltip popupmenu)]
    (.add system-tray icon)
    [icon popupmenu]))

(defn demo []
  (let [dot (make-dot "Demonstration")]
    (set-menu dot
              (dorothy.menu/label "Hello!")
              (dorothy.menu/divider)
              (dorothy.menu/button "Paint red"
                                   #(paint dot Color/RED))
              (dorothy.menu/button "Paint green"
                                   #(paint dot Color/GREEN))
              (dorothy.menu/button "Paint blue"
                                   #(paint dot Color/BLUE))
              (dorothy.menu/divider)
              (dorothy.menu/button "Paint random"
                                   #(paint dot (Color. (rand-int (* 256 256 256)))))
              (dorothy.menu/divider)
              (dorothy.menu/button "Exit" #(destroy dot)))))
