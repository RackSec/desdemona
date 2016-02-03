(ns desdemona.query
  (:require [clojure.core.logic :as l]))

(defn ^:private gen-query
  [n-answers query events]
  `(l/run ~n-answers [results#]
     (l/fresh [~'x]
       (l/== [~'x] results#)
       (l/membero ~'x ~events)
       ~query)))

(defn run-query
  ([query events]
   (run-query 1 query events))
  ([n-answers query events]
   (eval (gen-query n-answers query events))))
