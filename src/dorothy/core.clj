(ns dorothy.core
  (:require [dorothy.system :refer [system-tray]]
            [dorothy.image :refer [make-image empty-image]])
  (:import (java.awt TrayIcon PopupMenu)))


(defn paint
  "Repaint the dot with the given colour."
  [[icon _] colour]
  (when icon
    (.setImage icon (make-image colour))))

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
