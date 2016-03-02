(ns desdemona.functions.sample-functions-test
  (:require [clojure.test :refer [deftest is]]
            [desdemona.functions.sample-functions :refer [prepare-rows message-origin add-message-origin build-row]]))

(deftest prepare-rows-test
  (let [got (prepare-rows {"line" "this is a log line"})
        expected {:rows [{"line" "this is a log line"}]}]
    (is (= got expected))))

(def example-syslog {"TAGS" ".source.s_tcp" "SOURCEIP" "192.168.99.1" "PROGRAM" "sean" "PRIORITY" "alert" "PID" "11423" "MESSAGE" "hello ernie, it's 2016-02-29 15:06:02.614728886 -0600 CST" "HOST" "192.168.99.1" "FACILITY" "kern" "DATE" "Feb 29 15:06:02"})

(def example-json {"_parsed" {"xyz" "well hello, this is json" "time" "2016-02-29T15:05:02.499771543-06:00" "nest" {"two" "22" "one" "1"}} "TAGS" ".source.s_tcp" "SOURCEIP" "192.168.99.1" "PROGRAM" "sean" "PRIORITY" "alert" "PID" "11305" "MESSAGE" "{\"nest\":{\"one\":1,\"two\":22},\"time\":\"2016-02-29T15:05:02.499771543-06:00\",\"xyz\":\"well hello, this is json\"}" "HOST" "192.168.99.1" "FACILITY" "kern" "DATE" "Feb 29 15:05:02"})

(def example-falconhose {"_parsed" {"metadata" {"offset" "6342" "eventType" "UserActivityAuditEvent" "customerIDString" "3d7acbfa452843c4a2fdf8b4a30514f5"} "event" {"UserIp" "50.57.17.245" "UserId" "Eray.Ozugur@rackspace.com" "UTCTimestamp" "1456779715" "Success" "true" "ServiceName" "Detects" "OperationName" "UpdateDetectState" "AuditKeyValues[1]" {"ValueString" "in_progress" "Key" "new_state"},"AuditKeyValues[0]" {"ValueString" "-1001162664261133112" "Key" "detects"}}} "TAGS" ".source.s_tcp" "SOURCEIP" "172.17.0.10" "PROGRAM" "falcon" "PRIORITY" "err" "PID" "1" "MESSAGE" "{\"metadata\":{\"customerIDString\":\"3d7acbfa452843c4a2fdf8b4a30514f5\",\"offset\":6342,\"eventType\":\"UserActivityAuditEvent\"},\"event\":{\"UserId\":\"Eray.Ozugur@rackspace.com\",\"UserIp\":\"50.57.17.245\",\"OperationName\":\"UpdateDetectState\",\"ServiceName\":\"Detects\",\"Success\":true,\"AuditKeyValues\":[{\"Key\":\"detects\",\"ValueString\":\"-1001162664261133112\"},{\"Key\":\"new_state\",\"ValueString\":\"in_progress\"}],\"UTCTimestamp\":1456779715}}\r" "HOST" "172.17.0.10" "FACILITY" "kern" "DATE" "Feb 29 21:01:58"})

(def example-cloudpassage {"_parsed" {"type" "halo_login_success" "name" "Halo login success" "message" "Halo user travis.mercier+magento logged into the Halo Portal using browser Chrome on Windows from IP address 50.56.229.15 (USA)." "id" "4c993600b4c011e59a750d56cb124118" "critical" "false" "created_at" "2016-01-06T21:56:11.657Z" "actor_username" "travis.mercier+magento" "actor_ip_address" "50.56.229.15" "actor_country" "USA"} "TAGS" ".source.s_tcp" "SOURCEIP" "192.168.99.1" "PROGRAM" "sean" "PRIORITY" "alert" "PID" "13898" "MESSAGE" "{\"server_id\":null,\"actor_country\":\"USA\",\"actor_ip_address\":\"50.56.229.15\",\"name\":\"Halo login success\",\"type\":\"halo_login_success\",\"actor_username\":\"travis.mercier+magento\",\"critical\":false,\"id\":\"4c993600b4c011e59a750d56cb124118\",\"created_at\":\"2016-01-06T21:56:11.657Z\",\"message\":\"Halo user travis.mercier+magento logged into the Halo Portal using browser Chrome on Windows from IP address 50.56.229.15 (USA).\"}" "HOST" "192.168.99.1" "FACILITY" "kern" "DATE" "Feb 29 15:57:54"})

(deftest message-origin-test
  (is (= :syslog (message-origin example-syslog)))
  (is (= :json (message-origin example-json)))
  (is (= :falconhose (message-origin example-falconhose)))
  (is (= :cloudpassage (message-origin example-cloudpassage))))

(deftest add-message-origin-test
  (let [got (add-message-origin example-syslog)]
    (is (= :syslog (:origin got)))))

(deftest build-row-test
  (let [input {:origin :somewhere "MESSAGE" "This is the message!"}
        expected {:line "somewhere: This is the message!"}
        got (build-row input)]
    (is (= got expected))))
