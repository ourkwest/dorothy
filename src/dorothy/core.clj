(ns dorothy.core
  (:import (java.awt SystemTray TrayIcon Color RenderingHints MenuItem PopupMenu)
           (java.awt.image BufferedImage)))


(def ^:private system-tray
  "The java.awt.SystemTray instance for the running JVM"
  (when (SystemTray/isSupported) (SystemTray/getSystemTray)))

(def ^:private icon-size
  "Size and inset for drawing icons."
  (let [dimension (.getTrayIconSize system-tray)
        width (int (.getWidth dimension))
        height (int (.getHeight dimension))
        size (min width height)
        inset 2]
    [size inset]))

(defn- distance
  "Calculates the distance between two points."
  [x1 y1 x2 y2]
  (let [dx (- x1 x2)
        dy (- y1 y2)]
    (Math/sqrt (+ (* dx dx) (* dy dy)))))

(def ^:private shading
  "A shaded image to be overlaid onto a dot giving the appearance of sphericality."
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
    image))

(defn- make-image
  "Create an image that can be used in a dot."
  [colour]
  (let [[size inset] icon-size
        image (BufferedImage. size size BufferedImage/TYPE_INT_ARGB)
        graphics (.getGraphics image)]
    (.setRenderingHint graphics RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON)
    (.setColor graphics colour)
    (.fillArc graphics inset inset (- size inset inset) (- size inset inset) 0 360)
    (.drawImage graphics shading 0 0 nil)
    image))

(def ^:private empty-image
  "A 'null object' for icon images."
  (make-image (Color. 0 0 0 0)))

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
