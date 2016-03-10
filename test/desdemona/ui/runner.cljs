(ns desdemona.ui.runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [desdemona.ui.core-test]))

(doo-tests
 'desdemona.ui.core-test)
