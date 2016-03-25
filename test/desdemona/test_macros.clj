(ns desdemona.test-macros
  (:import
   [java.io StringWriter]))

(defmacro with-out-str-and-result
  [& body]
  `(let [stdout# (StringWriter.)]
     (binding [*out* stdout#]
       (let [result# (do ~@body)]
         [result# (str stdout#)]))))
