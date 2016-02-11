(ns dorothy.image
  (:require [dorothy.system :refer [system-tray]]
            [dorothy.lookup :refer [emoji-lookup]])
  (:import (java.awt Color RenderingHints)
           (java.awt.image BufferedImage)
           (javax.imageio ImageIO)
           (java.net URL)
           (java.io File)))


(def ^:private icon-size
  "Size and inset for drawing icons."
  (when system-tray
    (let [dimension (.getTrayIconSize system-tray)
          width (.width dimension)
          height (.height dimension)
          size (min width height)
          inset 2]
      [size inset])))

(defn- distance
  "Calculates the distance between two points."
  [x1 y1 x2 y2]
  (let [dx (- x1 x2)
        dy (- y1 y2)]
    (Math/sqrt (+ (* dx dx) (* dy dy)))))

(def ^:private shading
  "A shaded image to be overlaid onto a dot giving the appearance of sphericality."
  (when icon-size
    (let [[size inset] icon-size
          image (BufferedImage. size size BufferedImage/TYPE_INT_ARGB)
          graphics (.getGraphics image)
          half (/ size 2)
          third (/ size 3)
          inner-half (- half inset)]
      (.setRenderingHint graphics RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON)
      (doseq [x (range size)
              y (range size)]
        (let [to-centre (distance half half x y)]
          (when (< to-centre inner-half)
            (let [to-highlight (distance third third x y)]
              (cond
                ; Highlight
                (< to-highlight inner-half) (let [alpha (int (* 200 (- 1 (/ to-highlight inner-half))))]
                                              (.setRGB image x y (.getRGB (Color. 255 255 255 alpha))))
                ; Shade
                (> to-highlight inner-half) (let [alpha (int (* 200 (/ (- to-highlight inner-half) third)))]
                                              (.setRGB image x y (.getRGB (Color. 0 0 0 alpha)))))))))
      ; Border
      (.setColor graphics Color/BLACK)
      (.drawArc graphics inset inset (- size inset inset) (- size inset inset) 0 360)
      image)))

(defn make-image
  "Create an image that can be used in a dot."
  [colour]
  (when icon-size
    (let [[size inset] icon-size
          image (BufferedImage. size size BufferedImage/TYPE_INT_ARGB)
          graphics (.getGraphics image)]
      (.setRenderingHint graphics RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON)
      (.setColor graphics colour)
      (.fillArc graphics inset inset (- size inset inset) (- size inset inset) 0 360)
      (.drawImage graphics shading 0 0 nil)
      image)))

(def empty-image
  "A 'null object' for icon images."
  (make-image (Color. 0 0 0 0)))


(defn get-image [name]
  (let [filename (str "./resources/png/" (emoji-lookup name) ".png")
        file (File. filename)]
    (println filename)
    (println file)
    (ImageIO/read file)))
