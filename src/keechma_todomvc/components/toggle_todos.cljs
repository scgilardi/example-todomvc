(ns keechma-todomvc.components.toggle-todos
  "# Toggle Todos component"
  (:require [sazhet.ui :refer [<checked <cmd <comp sub>]
             :refer-macros [evt>]]))

(defn render
  "## Renders a checkbox element

  The checkbox toggles the `status` of all `todos` between `:completed`
  and `:active`. If the `status` of the `todos` is a mixture of
  `:completed` and `:active`, sets them all to `:completed`.

### Subscription Deps

- `has-todos-by-status?` returns true if there are any `todos` with a `status`"
  [ctx]
  [:<>
   [:input#toggle-all.toggle-all
    {:type :checkbox
     :on-change (evt> event (<cmd ctx :toggle-all (<checked event)))
     :checked (not (sub> ctx :has-todos-by-status? :active))}]
   [:label {:for :toggle-all} "Mark all as completed"]])

(def component
  (<comp :renderer render
         :subscription-deps [:has-todos-by-status?]))
