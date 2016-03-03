(ns desdemona.utils-test
  (:require
   [desdemona.utils :as u]
   [clojure.test :refer [deftest are testing]]))

(deftest str->kw-test
  (are [in out] (= (u/str->kw in) out)
    "abc" :abc
    "abc def" :abc-def
    "abc-def" :abc-def
    "abc_def" :abc-def
    "ABC_DEF" :abc-def
    "abc-def." :abc-def
    "abc-def!" :abc-def!
    "abc-def?" :abc-def?
    "-abc-def" :abc-def))

(deftest kwify-map-test
  (are [in out] (= (u/kwify-map in) out)
    {} {}
    {"abc" "abc"} {:abc "abc"}
    {"abc def" "abc def"} {:abc-def "abc def"}
    {"-abc def" "abc def"} {:abc-def "abc def"}))
