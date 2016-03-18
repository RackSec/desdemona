(ns desdemona.query
  (:require
   [clojure.core.logic :as l]
   [clojure.core.match :as m]))

(defn ^:private generate-logic-query
  "Expands a query and events to a core.logic program that executes
  it.

  This is implemented using syntax-quote because that was the easiest
  way to produce this data structure with some values interpolated.

  The n-answers is simply passed to core.logic/run; we're relying on
  it to correctly bound the number of answers. This helps us limit how
  long it takes to query."
  [n-answers logic-query events]
  `(l/run ~n-answers [results#]
     (l/fresh [~'x] ;; ~'x means "literally x, don't gensym", see #28
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
  (m/match [dsl-query]
    [((= ((attr lvar) :seq) value) :seq)]
    `(l/featurec ~lvar {~attr ~value})

    [((= value ((attr lvar) :seq)) :seq)]
    `(l/featurec ~lvar {~attr ~value})

    [(['and & terms] :seq)]
    (let [logic-terms (map dsl->logic terms)]
      `(l/conde [~@logic-terms]))))

(defn run-dsl-query
  "Run a DSL query over some events and finds n answers (default 1)."
  ([dsl-query events]
   (run-logic-query (dsl->logic dsl-query) events))
  ([n-answers dsl-query events]
   (run-logic-query n-answers (dsl->logic dsl-query) events)))
