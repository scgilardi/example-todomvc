(ns keechma-todomvc.ui)

;; ## Events

(defmacro evt>
  "Wraps a function body with an event handler. If the first item is a
   symbol, binds the event to it, otherwise the event is ignored."
  [& body]
  (let [[event body] (if (symbol? (first body))
                       [(first body) (rest body)]
                       [(gensym) body])]
    `(fn [~event]
       (when-not (.-defaultPrevented ~event)
         ~@body
         (.preventDefault ~event)))))
