(ns desdemona.test-macros)

(defmacro with-out-str-and-result
  [& body]
  `(let [stdout# (StringWriter.)]
     (binding [*out* stdout#]
       (let [result# (do ~@body)]
         [result# (str stdout#)]))))
