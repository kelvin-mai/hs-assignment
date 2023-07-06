(ns app.components.core
  (:require [re-frame.core :as rf]
            [app.util.theme :refer [theme]]
            [app.components.patient-table :refer [patient-table]]
            [app.db.router :as router.db]
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
  (let [current-route @(rf/subscribe [::router.db/current-route])]
    [:> ThemeProvider {:theme theme}
     [:> CssBaseline]
     [navbar]
     [:main
      [:> Box {:sx {:mt 2
                    :m 4}}
       [:> Paper {:width "100%"}
        (when current-route
          [(-> current-route :data :view)])]]]]))
