(ns dorothy.core
  (:require [dorothy.system :refer [system-tray]]
            [dorothy.image :refer [make-image empty-image get-image]])
  (:import (java.awt TrayIcon PopupMenu Color)))


(defn- paint-colour
  "Repaint the dot with the given colour."
  [icon colour]
  (.setImage icon (make-image colour)))

(defn- paint-emoji
  "Repaint the dot with the image of an emoji."
  [icon emoji]
  (.setImage icon (get-image emoji)))

(defn paint
  "Repaint the dot with the given colour."
  [[icon _] colour-or-emoji]
  (when icon
    (cond
      (keyword? colour-or-emoji) (paint-emoji icon colour-or-emoji)
      (integer? colour-or-emoji) (paint-colour icon (Color. colour-or-emoji))
      (instance? Color colour-or-emoji) (paint-colour icon colour-or-emoji))))

(defn destroy
  "Remove the dot from the system tray."
  [[icon _]]
  (when (and system-tray icon)
    (.remove system-tray icon)))

(defn set-menu
  "Set the menu on this dot."
  [[_ popupmenu] & menuitems]
  (when popupmenu
    (.removeAll popupmenu)
    (doseq [menuitem menuitems]
      (.add popupmenu menuitem))))

(defn make-dot
  "Makes a dot and adds it to the system tray."
  [tooltip]
  (when system-tray
    (let [popupmenu (PopupMenu.)
          icon (TrayIcon. empty-image tooltip popupmenu)]
      (.add system-tray icon)
      [icon popupmenu])))
