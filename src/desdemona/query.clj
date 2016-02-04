(ns desdemona.query
  (:require [clojure.core.logic :as l]))

(defn ^:private generate-logic-query
  "Expands a query and events to a core.logic program that executes
  it."
  [n-answers query events]
  `(l/run ~n-answers [results#]
     (l/fresh [~'x]
       (l/== [~'x] results#)
       (l/membero ~'x ~events)
       ~query)))

(defn run-logic-query
  "Runs a query over some events and finds n answers (default 1)."
  ([query events]
   (run-query 1 query events))
  ([n-answers query events]
   (let [compiled-query (generate-logic-query n-answers query events)
         old-ns *ns*]
     (try
       (in-ns 'desdemona.query)
       (eval compiled-query)
       (finally
         (in-ns (ns-name old-ns)))))))
