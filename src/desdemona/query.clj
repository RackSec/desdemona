(ns desdemona.query
  (:require [clojure.core.logic :as l]))

(defn ^:private gen-query
  "Expands a query and events to a core.logic program that executes
  it.

  This is implemented using syntax-quote because that was the easiest
  way to produce this data structure with some values interpolated."
  [n-answers query events]
  `(l/run ~n-answers [results#]
     (l/fresh [~'x] ;; ~'x means "literally x, don't gensym", see #28
       (l/== [~'x] results#)
       (l/membero ~'x ~events)
       ~query)))

(defn run-query
  "Runs a query over some events and finds n answers (default 1)."
  ([query events]
   (run-query 1 query events))
  ([n-answers query events]
   (let [compiled-query (gen-query n-answers query events)
         old-ns *ns*]
     (try
       (in-ns 'desdemona.query)
       (eval compiled-query)
       (finally
         (in-ns (ns-name old-ns)))))))
