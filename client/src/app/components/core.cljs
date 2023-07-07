(ns app.components.core
  (:require [re-frame.core :as rf]
            [app.util.theme :refer [theme]]
            [app.db.router :as router.db]
            [app.db.ui :as ui.db]
            ["@mui/material/styles" :refer [ThemeProvider]]
            ["@mui/material" :refer [AppBar
                                     Alert
                                     Box
                                     CssBaseline
                                     Divider
                                     Drawer
                                     IconButton
                                     List
                                     ListItem
                                     ListItemButton
                                     ListItemIcon
                                     ListItemText
                                     Paper
                                     Snackbar
                                     Toolbar
                                     Typography]]
            ["@mui/icons-material" :refer [Dataset
                                           Menu
                                           PersonAdd]]))

(defn navbar []
  [:> AppBar {:position "static"}
   [:> Toolbar
    [:> IconButton {:size "large"
                    :edge "start"
                    :color "inherit"
                    :sx {:mr 2}
                    :on-click #(rf/dispatch [::ui.db/set-menu true])}
     [:> Menu]]
    [:> Typography {:variant "h6"
                    :component "div"
                    :sx {:flex-grow 1}}
     "Health Samurai Assignment"]]])

(defn navlink [{:keys [label link icon]}]
  ^{:key link}
  [:> ListItem {:disable-padding true}
   [:> ListItemButton {:on-click #(rf/dispatch [::ui.db/push-state link])}
    [:> ListItemIcon
     [:> icon]]
    [:> ListItemText {:primary label}]]])

(defn navmenu []
  (let [open @(rf/subscribe [::ui.db/menu-open?])]
    [:> Drawer {:anchor "left"
                :open (or open false)
                :on-close #(rf/dispatch [::ui.db/set-menu false])}
     [:> Box {:sx {:width 250}}
      [:> Toolbar]
      [:> Divider]
      [:> List
       (map
        navlink
        [{:label "Patient Table"
          :link :app.router/home
          :icon Dataset}
         {:label "New Patient"
          :link :app.router/new-patient
          :icon PersonAdd}])]]]))

(defn app []
  (let [current-route @(rf/subscribe [::router.db/current-route])
        {:keys [open
                severity
                message]} @(rf/subscribe [::ui.db/alert])]
    [:> ThemeProvider {:theme theme}
     [:> CssBaseline]
     [navbar]
     [navmenu]
     [:> Snackbar {:open open
                   :anchor-origin {:vertical "top"
                                   :horizontal "center"}
                   :auto-hide-duration 2000
                   :on-close #(rf/dispatch [::ui.db/close-alert])}
      [:> Alert {:severity severity} message]]
     [:main
      [:> Box {:sx {:mt 2
                    :m 4}}
       [:> Paper {:width "100%"}
        (when current-route
          [(-> current-route :data :view)])]]]]))
