(ns views.db.load-test
  (:require
   [clojure.test :refer [use-fixtures deftest is]]
   [honeysql.core :as hsql]
   [views.fixtures :as vf :refer [gen-n-users! database-fixtures! templates]]
   [views.db.load :as vload]
   [clojure.string :refer [upper-case]]))

(use-fixtures :each database-fixtures!)

(defn subscribed-views
  []
  {[:users] {:view-map ((get-in templates [:users :fn]))}})

(deftest initializes-views
  (let [users (gen-n-users! 2)]
    (is (= (vload/initial-views vf/db [[:users]] templates (subscribed-views))
           {[:users] users}))))

(deftest post-processes-views
  (let [users       (gen-n-users! 1)
        with-postfn (assoc-in templates [:users :post-fn] #(update-in % [:name] upper-case))
        views-rs    (vload/initial-views vf/db [[:users]] with-postfn (subscribed-views))]
    (is (= (-> (get views-rs [:users]) first :name)
           (-> users first :name upper-case)))))
