(ns keechma-todomvc.components.todo-edit
  "# Todo Edit component"
  (:require [keechma-todomvc.ui :refer [<cmd <comp sub> on-key> on-value>]
             :refer-macros [evt>]]
            [reagent.core :as reagent]))

(defn form-3-render
  "## Renders an Edit Field

### Form 3 render function

  Returns a `reagent class` created from a map that allows us to
  provide additional component data to `reagent` including
  implementations for `react lifecycle functions` like
  `:component-did-mount`.

### Subscription Deps

- `:edit-todo` returns the `todo` currently being edited, or nil

### Note

  This component is using a `form 3` render function to demonstrate
  the additional flexibility it allows. In this case, we could get the
  same effect by setting the `:auto-focus` attribute on the
  `:input.edit` element to `true` within a `form 2` render function."
  [ctx]
  (let [edit-todo (sub> ctx :edit-todo)
        todo-title (reagent/atom (:title edit-todo))
        update (evt> (<cmd ctx :confirm-edit
                           (assoc edit-todo :title @todo-title)))
        cancel (evt> (<cmd ctx :cancel-edit))
        render (fn []
                 [:input.edit {:value @todo-title
                               :on-blur update
                               :on-change (on-value> todo-title)
                               :on-key-down (on-key> :enter update
                                                     :escape cancel)}])
        focus-input #(let [node (reagent/dom-node %)
                           length (count (.-value node))]
                       (.focus node)
                       (.setSelectionRange node length length))]
    (reagent/create-class
     {:display-name "todo-edit"
      :reagent-render render
      :component-did-mount focus-input})))

(def component
  (<comp :renderer form-3-render
         :subscription-deps [:edit-todo]))
