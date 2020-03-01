(ns keechma-todomvc.controllers
  "# Controllers"
  (:require [keechma-todomvc.controllers.todos :as todos]))

(def controllers
  {:todos (todos/->Controller)})
