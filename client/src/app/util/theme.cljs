(ns app.util.theme
  (:require ["@mui/material/styles" :refer [createTheme]]))

(def theme
  (createTheme
   (clj->js
    {:palette {:mode "dark"}})))
