(ns keechma-todomvc.ui
  "# UI Convenience functions

  Adapted from and inspired by `keechma.toolbox.ui`."
  (:require [clojure.string :as str]
            [keechma.ui-component :as ui]))

;; ## Routes

(defn route>
  "Reads current route `data` from `ctx`. `args` are an inline `key
  path` within the route map's `:data` value."
  [ctx & args]
  (get-in (:data @(ui/current-route ctx)) args))

(defn <url
  "Constructs a `url` string from `ctx` and a `map` or inline `keys` and
  `values` that specify the `data` that the `url`'s `route` should
  resolve to."
  ([ctx kvs]
   (ui/url ctx kvs))
  ([ctx k v & {:as kvs}]
   (<url ctx (assoc kvs k v))))

;; ## Subscriptions

(defn sub>
  "Returns a reaction on a data subscription."
  [ctx subscription & args]
  @(ui/subscription ctx subscription args))

;; ## Components

;; ### Keechma

(defn <comp
  "Creates a `Keechma component` from a `map` or inline `keys` and
  `values`. Keys: :renderer, :component-deps, :subscription-deps"
  ([kvs]
   (ui/constructor kvs))
  ([k v & {:as kvs}]
   (<comp (assoc kvs k v))))

;; ### Reagent

(defn comp>
  "Constructs a `Reagent component` specified by `key` and optional
  `args`. Any `args` are gathered into an explicit vector for
  `ui/component`."
  [ctx key & args]
  (apply vector (ui/component ctx key) args))

(defn map>
  "Returns a realized `seq` of `Reagent components`. For each `item` in `coll`,
  creates a `Reagent component` by calling `f` with `item` as the first
  argument. Any additional arguments are the result of calling each
  `arg-fn` on `item`. Each `component` is marked with a unique `key`
  for `react` via metadata. The unique `key` is produced by applying a
  `key-fn` to `item`. The default `key-fn` is `:id`. To specify a
  different `key-fn`, provide it as `:map>/key-fn` metadata on
  `coll`."
  [f coll & arg-fns]
  (let [key-fn (:map>/key-fn (meta coll) :id)
        args-fn (if arg-fns (apply juxt arg-fns) (constantly nil))
        comp-fn (fn [item]
                  (with-meta
                    (apply f item (args-fn item))
                    {:key (key-fn item)}))]
    (seq (into [] (map comp-fn) coll))))

(defn comps>
  "Returns a realized `seq` of `Reagent components`. For each `item` in `coll`,
  creates a `Reagent component` by calling `comp>` with `ctx`, `key`, and
  `item` as the first 3 arguments. Any additional arguments are the
  results of calling each `arg-fn` on `item`. Each `component` is
  marked with a unique `key` for `react` via metadata. The unique
  `key` is produced by applying a `key-fn` to `item`. The default
  `key-fn` is `:id`. To specify a different `key-fn`, provide it as
  `:map>/key-fn` metadata on `coll`."
  [ctx key coll & arg-fns]
  (apply map> (partial comp> ctx key) coll arg-fns))

;; ## Commands

(defn <cmd
  "Sends an asynchronous `command` via `ctx`. Gathers any `args`
  beyond the `command` into an explicit `vector` for transport."
  [ctx command & args]
  (ui/send-command ctx command args))

;; ## Events

(defn on-key>
  "Returns a function to dispatch a `KeyboardEvent` to an event
  `handler` based on its `KeyboardEvent.key` value. The `key-handlers`
  mapping can be passed in as a `map` or inline `key`s and `value`s.

  Each entry in `key-handlers` maps a `keyword` to a `handler`. A
  `keyword` matches a `KeyboardEvent.key` value if `(name keyword)` is
  either `key` or `(lower-case key)`. For example, the keywords
  `:Enter` and `:enter` both match the `KeyboardEvent.key` value
  \"Enter\".

  On a match, calls the `handler` with event as an argument."
  ([key-handlers]
   (fn [event]
     (when-not (.-defaultPrevented event)
       (let [key-name (.-key event)
             exact-kw (keyword key-name)
             lower-kw (keyword (str/lower-case key-name))]
         (when-let [handler (or (key-handlers exact-kw)
                                (key-handlers lower-kw))]
           (handler event)
           (.preventDefault event))))))
  ([key handler & {:as key-handlers}]
   (on-key> (assoc key-handlers key handler))))

(defn <value
  [event]
  (.. event -target -value))

(defn on-value>
  [atom]
  (fn [event]
    (reset! atom (<value event))))

(defn <checked
  [event]
  (.. event -target -checked))

(defn on-checked>
  [atom]
  (fn [event]
    (reset! atom (<checked event))))
