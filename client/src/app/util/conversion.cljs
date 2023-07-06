(ns app.util.conversion)

(defn stringify-kw [kw]
  (str (when (namespace kw)
         (str (namespace kw) "/"))
       (name kw)))

(defn same-sex-gender [sex]
  (if (= sex "male")
    "man"
    "woman"))
