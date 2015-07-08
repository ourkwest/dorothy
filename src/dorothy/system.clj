(ns dorothy.system
  (:import (java.awt SystemTray HeadlessException)))


(def system-tray
  "The java.awt.SystemTray instance for the running JVM"
  (when (SystemTray/isSupported)
    (try
      (SystemTray/getSystemTray)
      (catch HeadlessException _ nil))))
