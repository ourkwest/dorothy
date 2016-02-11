# dorothy

"Somewhere over the rainbow... weigh a pie."

A Clojure library to add dots to the system tray.

## Usage

Leiningen coordinates:
```[cotton/dorothy "1.0.1"]```

Example usage:
```clojure
(use 'dorothy.core) 
=> nil

(def my-dot (make-dot "My Dot"))
=> #'user/my-dot
(paint my-dot java.awt.Color/YELLOW)
=> nil

(use 'dorothy.menu) 
=> nil
(import java.awt.Color)
=> java.awt.Color

(set-menu my-dot
          (label "Hello")
          (divider)
          (submenu "Pre-defined"
                   (button "Red" #(paint my-dot Color/RED))
                   (button "Light Green" #(paint my-dot 0x88FF88))
                   (button "Banana" #(paint my-dot :banana)))
          (divider)
          (button "Random" #(paint my-dot (Color. (rand-int (* 256 256 256)))))
          (divider)
          (button "Go away!" #(destroy my-dot)))
=> nil
```

## License

Code distributed under the Eclipse Public License, the same as Clojure.

Emoji provided free by [Emoji One](http://emojione.com/) under [Creative Commons 4.0](https://creativecommons.org/licenses/by/4.0/legalcode).
