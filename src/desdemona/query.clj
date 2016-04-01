(ns desdemona.query
  (:require
   [clojure.core.logic :as l]
   [clojure.core.match :as m]
   [clojure.string :as s]
   [instaparse.core :as insta]
   [clojure.java.io :refer [resource]]))

(defn ^:private free-sym
  "Returns symbol, but marked as a free variable."
  [sym]
  (vary-meta sym assoc ::free true))

(def ^:private free-sym?
  "Check if an object has the free variable metadata annotation."
  (every-pred symbol? (comp ::free meta)))

(defn ^:private find-free-vars
  "Finds all the free logic variables in a given logic query.

  This takes advantage of the fact that function references for goal
  functions/macros (conde, featurec...) will be fully qualified, but free
  variables will be unadorned by a namespace."
  [logic-query]
  (->> (flatten logic-query)
       (filter free-sym?)
       set))

(defn ^:private generate-logic-query
  "Expands a query and events to a core.logic program that executes
  it.

  This is implemented using syntax-quote because that was the easiest
  way to produce this data structure with some values interpolated.

  The n-answers is simply passed to core.logic/run; we're relying on
  it to correctly bound the number of answers. This helps us limit how
  long it takes to query."
  [n-answers logic-query events]
  (let [free-vars (find-free-vars logic-query)
        membero-clauses (for [v free-vars]
                          `(l/membero ~v ~events))]
    `(l/run ~n-answers [results#]
       (l/fresh [~@free-vars]
         (l/== [~@free-vars] results#)
         ~@membero-clauses
         ~logic-query))))

(defn ^:private run-logic-query
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

(def dsl-literal?
  "Is this a literal in the DSL?"
  string?)

(defn ^:private dsl->logic
  "Given a DSL query, compile it to the underlying logic (miniKanren)
  expressions."
  [dsl-query]
  (m/match [dsl-query]
    [(('= & terms) :seq)]
    (let [{literals true attr-terms false} (group-by dsl-literal? terms)
          feature (fn [value [attr lvar]]
                    `(l/featurec ~(free-sym lvar) {~attr ~value}))]
      (m/match [(count literals) (count attr-terms)]
        [1 1] (feature (first literals) (first attr-terms))
        [1 _] `(l/all ~@(map (partial feature (first literals)) attr-terms))
        [0 _] (let [u (gensym)]
                `(l/fresh [~u] ~@(map (partial feature u) attr-terms)))))

    [(('and & terms) :seq)]
    (let [logic-terms (map dsl->logic terms)]
      `(l/conde [~@logic-terms]))

    [(('or & terms) :seq)]
    (let [clauses (map (comp vector dsl->logic) terms)]
      `(l/conde ~@clauses))))

(defn run-dsl-query
  "Run a DSL query over some events and finds n answers (default 1)."
  ([dsl-query events]
   (run-logic-query (dsl->logic dsl-query) events))
  ([n-answers dsl-query events]
   (run-logic-query n-answers (dsl->logic dsl-query) events)))

(def ^:private infix-parser
  (insta/parser (resource "infix-query-grammar.ebnf")))

(defn ^:private parsed-infix->dsl
  [parsed]
  (m/match parsed
    [:expr terms]
    (parsed-infix->dsl terms)

    [:eq
     [:fn-call
      [:identifier "ip"]
      [:identifier arg]]
     [:ipv4-address & addr-parts]]
    (let [arg (symbol arg)
          addr (s/join "." addr-parts)]
      `(~'= (:ip ~arg) ~addr))))

(def infix->dsl
  (comp parsed-infix->dsl infix-parser))
