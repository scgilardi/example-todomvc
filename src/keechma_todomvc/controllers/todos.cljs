(ns keechma-todomvc.controllers.todos
  (:require [keechma.controller :as controller :refer [dispatcher]]
            [cljs.core.async :refer [<!]]
            [keechma-todomvc.edb :as edb]
            [keechma-todomvc.entities.todo :as todo])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(defn updater!
  "Commits the change to the app-db to the app-db atom."
  [modifier-fn]
  (fn [app-db-atom args]
    (swap! app-db-atom modifier-fn args)))

(defrecord ^{:doc "
This controller receives the commands from the UI and dispatches
them to the functions that modify the state.

- `params` function returns true because this controller should always be running
- `start` function adds an empty todo list to the EntityDB
- `handler` function dispatches commands from the UI to the modifier functions"} 
  Controller []
  controller/IController
  (params [_ _] true)
  (start [_ params app-db]
    (edb/insert-collection app-db :todos :list []))
  (handler [_ app-db-atom in-chan _]
    (dispatcher app-db-atom in-chan
                {:toggle-todo (updater! todo/toggle-todo)
                 :create-todo (updater! todo/create-todo)
                 :update-todo (updater! todo/update-todo)
                 :destroy-todo (updater! todo/destroy-todo)
                 :edit-todo (updater! todo/edit-todo)
                 :cancel-edit-todo (updater! todo/cancel-edit-todo)
                 :destroy-completed (updater! todo/destroy-completed)
                 :toggle-all (updater! todo/toggle-all)})))
