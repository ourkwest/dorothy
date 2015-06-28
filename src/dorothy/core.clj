(ns dorothy.core
  (:import (java.awt SystemTray TrayIcon Color RenderingHints MenuItem PopupMenu)
           (java.awt.image BufferedImage)))


(def ^:private system-tray (when (SystemTray/isSupported) (SystemTray/getSystemTray)))

(def ^:private icon-size (let [dimension (.getTrayIconSize system-tray)
                               width (int (.getWidth dimension))
                               height (int (.getHeight dimension))
                               inset 3]
                           [width height inset]))

(defn- make-image [colour]
  (let [[width height inset] icon-size
        image (BufferedImage. width height BufferedImage/TYPE_INT_ARGB)
        graphics (.getGraphics image)]
    (.setRenderingHint graphics RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON)
    (.setColor graphics colour)
    (.fillArc graphics inset inset (- width inset inset) (- height inset inset) 0 360)
    (.setColor graphics Color/BLACK)
    (.drawArc graphics inset inset (- width inset inset) (- height inset inset) 0 360)
    image))

(def ^:private empty-image
  (make-image (Color. 0 0 0 0)))

(defprotocol Dot
  (paint [Dot colour])
  (destroy [Dot]))

(deftype Dorothy [icon]
  Dot
  (paint [_ colour]
    (.setImage icon (make-image colour)))
  (destroy [_]
    (.remove system-tray icon)))

(defn make-dot [tooltip]
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
