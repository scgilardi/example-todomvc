(ns keechma-todomvc.components.todo-list
  "# Todo List component"
  (:require [sazhet.ui :refer [<comp comps> route> sub>]]))

(defn render
  "## Renders a list of currently visible todos

  `todo` visiblity is controlled by the current `route`.

### Component Deps

- `:todo-item` Each list item is rendered by a `:todo-item` component
  that receives the `todo` and the calculated `is-editing?` value as
  arguments.

### Subscription Deps

- `:todos-by-status` returns `todos` with a `status`
- `:edit-todo` returns the `todo` currently being edited, or nil"
  [ctx]
  (let [route-status (keyword (route> ctx :status))
        todos (sub> ctx :todos-by-status route-status)
        >is-editing? #(= (:id %) (:id (sub> ctx :edit-todo)))]
    [:ul.todo-list
     (comps> ctx :todo-item todos >is-editing?)]))

(def component
  (<comp :renderer render
         :component-deps [:todo-item]
         :subscription-deps [:todos-by-status
                             :edit-todo]))
