(ns keechma-todomvc.components.footer
  "# Footer component"
  (:require [clojure.string :as str]
            [keechma-todomvc.ui :refer [<cmd <comp map> route> sub> <url]
             :refer-macros [evt>]]))

(def statuses ^{:map>/key-fn keyword}
  ["all" "active" "completed"])

(defn render
  "## Renders the Footer

- the active `todo` count
- buttons to filter the `todo` list by status
- the `clear-completed` button if there are any completed `todos`

### Route Data

  Reads the `route status` from the current route `data` and uses it
  to highlight the correct filter button as selected.

### Subscription Deps

  Each `todo` has a `status` of either `:completed` or `:active`.

- `:count-todos-by-status` returns the count of `todos` with a `status`
- `:has-todos-by-status?` returns `true` if there are any `todos` with
  a `status`."
  [ctx]
  (let [active-count (sub> ctx :count-todos-by-status :active)
        count-label (str " item" ({1 ""} active-count "s") " left")
        filter-item (fn [status]
                      (let [href (<url ctx :status status)
                            class (when (= (route> ctx :status) status)
                                    :selected)
                            label (str/capitalize status)]
                        [:li>a {:href href :class class} label]))]
    [:footer.footer
     [:span.todo-count
      [:strong active-count] count-label]
     [:ul.filters (map> filter-item statuses)]
     (when (sub> ctx :has-todos-by-status? :completed)
       [:button.clear-completed
        {:on-click (evt> (<cmd ctx :delete-completed))}
        "Clear completed"])]))

(def component
  (<comp :renderer render
         :subscription-deps [:count-todos-by-status
                             :has-todos-by-status?]))
