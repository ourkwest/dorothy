(ns dorothy.system
  (:import (java.awt SystemTray)))


(def system-tray
  "The java.awt.SystemTray instance for the running JVM"
  (when (SystemTray/isSupported) (SystemTray/getSystemTray)))
