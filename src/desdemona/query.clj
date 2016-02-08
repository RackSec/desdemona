(ns desdemona.query
  (:require [clojure.core.logic :as l]
            [clojure.core.match :as m]
            [clojure.core.logic.fd :as fd]))

(defn ^:private generate-logic-query
  "Expands a query and events to a core.logic program that executes
  it."
  [n-answers logic-query events]
  `(l/run ~n-answers [results#]
     (l/fresh [~'x]
       (l/== [~'x] results#)
       (l/membero ~'x ~events)
       ~logic-query)))

(defn run-logic-query
  "Runs a query over some events and finds n answers (default 1)."
  ([logic-query events]
   (run-logic-query 1 logic-query events))
  ([n-answers logic-query events]
   (let [old-ns *ns*]
     (try
       (in-ns 'desdemona.query)
       (eval (generate-logic-query n-answers logic-query events))
       (finally
         (in-ns (ns-name old-ns)))))))

(defn ^:private dsl->logic
  "Given a DSL query, compile it to the underlying logic (miniKanren)
  expressions."
  [dsl-query]
  (m/match
   [dsl-query]

   [((= ([attr lvar] :seq) value) :seq)]
   `(l/featurec ~lvar {~attr ~value})))

(defn run-dsl-query
  [dsl-query events]
  (run-logic-query (dsl->logic dsl-query) events))
