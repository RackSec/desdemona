(ns desdemona.ui.runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [desdemona.ui.core-test]
   [desdemona.ui.nav-test]
   [desdemona.ui.dashboard-test]
   [desdemona.ui.table-test]))

(doo-tests
 'desdemona.ui.core-test
 'desdemona.ui.nav-test
 'desdemona.ui.dashboard-test
 'desdemona.ui.table-test)
