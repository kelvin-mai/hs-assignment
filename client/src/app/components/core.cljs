(ns app.components.core
  (:require [app.util.theme :refer [theme]]
            [app.components.patient-table :refer [patient-table]]
            ["@mui/material/styles" :refer [ThemeProvider]]
            ["@mui/material" :refer [AppBar
                                     Box
                                     CssBaseline
                                     Paper
                                     Toolbar
                                     Typography]]))

(defn navbar []
  [:> AppBar {:position "static"}
   [:> Toolbar
    [:> Typography {:variant "h6"
                    :component "div"
                    :sx {:flex-grow 1}}
     "Health Samurai Assignment"]]])

(defn app []
  [:> ThemeProvider {:theme theme}
   [:> CssBaseline]
   [navbar]
   [:main
    [:> Box {:sx {:mt 2
                  :m 4}}
     [:> Paper {:width "100%"}
      [patient-table]]]]])
