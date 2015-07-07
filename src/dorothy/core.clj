(ns dorothy.core
  (:require [dorothy.system :refer [system-tray]]
            [dorothy.image :refer [make-image empty-image]])
  (:import (java.awt TrayIcon Color MenuItem PopupMenu)))


(defprotocol Dot
  "Represents a mutable dot in the system tray."
  (paint [Dot colour] "Repaint the dot with the given colour.")
  (destroy [Dot] "Remove the dot from the system tray."))

(deftype Dorothy [icon]
  Dot
  (paint [_ colour]
    (.setImage icon (make-image colour)))
  (destroy [_]
    (.remove system-tray icon)))

(defn make-dot
  "Makes a dot and adds it to the system tray."
  [tooltip]
  (let [menu (doto (PopupMenu.)
               (.add (MenuItem. "Nothing to see here.")))
        icon (TrayIcon. empty-image tooltip menu)]
    (.add system-tray icon)
    (Dorothy. icon)))

(defn demo []
  (let [dot (make-dot "Demonstration")
        counter (atom 20)]
    (.start (Thread. #(do
                       (Thread/sleep 3000)
                       (while (< 0 (swap! counter dec))
                         (Thread/sleep 200)
                         (paint dot (rand-nth [Color/GREEN
                                               Color/RED
                                               (Color/decode "#FF99AA")
                                               (Color. (rand-int (* 256 256 256)))])))
                       (destroy dot))))))
