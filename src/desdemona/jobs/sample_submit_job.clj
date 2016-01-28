(ns desdemona.jobs.sample-submit-job
  (:require [clojure.java.io :refer [resource]]
            [com.stuartsierra.component :as component]
            [desdemona.workflows.sample-workflow :refer [workflow]]
            [desdemona.catalogs.sample-catalog :refer [build-catalog] :as sc]
            [desdemona.lifecycles.sample-lifecycle :refer [build-lifecycles] :as sl]
            [desdemona.flow-conditions.sample-flow-conditions :as sf]
            [desdemona.functions.sample-functions]
            [desdemona.dev-inputs.sample-input :as dev-inputs]
            [desdemona.utils :as u]
            [onyx.api]))

(defn submit-job [dev-env]
  (let [dev-cfg (-> "dev-peer-config.edn" resource slurp read-string)
        peer-config (assoc dev-cfg :onyx/id (:onyx-id dev-env))
        ;; Turn :read-lines and :write-lines into core.async I/O channels
        stubs [:read-lines :write-lines]
        ;; Stubs the catalog entries for core.async I/O
        dev-catalog (u/in-memory-catalog (build-catalog 20 50) stubs)
        ;; Stubs the lifecycles for core.async I/O
        dev-lifecycles (u/in-memory-lifecycles (build-lifecycles) dev-catalog stubs)]
    ;; Automatically pipes the data structure into the channel, attaching :done at the end
    (u/bind-inputs! dev-lifecycles {:read-lines dev-inputs/lines})
    (let [job {:workflow workflow
               :catalog dev-catalog
               :lifecycles dev-lifecycles
               :flow-conditions sf/flow-conditions
               :task-scheduler :onyx.task-scheduler/balanced}]
      (onyx.api/submit-job peer-config job)
      ;; Automatically grab output from the stubbed core.async channels,
      ;; returning a vector of the results with data structures representing
      ;; the output.
      (u/collect-outputs! dev-lifecycles [:write-lines]))))
