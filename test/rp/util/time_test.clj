(ns rp.util.time-test
  (:require [rp.util.time :as sut]
            [clojure.test :as t]))

(t/deftest test-parse-invalid-input
  (t/testing "invalid input"
    (t/is (not (sut/parse-instant "foo")))))

(t/deftest test-parse-instant
  (doseq [{:keys [in out]}
          [{:in  "2007-12-03T10:15:30Z"
            :out "2007-12-03T10:15:30Z"}
           {:in  "12/03/2007 10:15:30"
            :out "2007-12-03T15:15:30Z"}
           {:in  "08/03/2007 10:15:30"
            :out "2007-08-03T14:15:30Z"}
           {:in  "01-MAR-2007"
            :out "2007-03-01T17:00:00Z"}
           {:in  "10-MAR-2007"
            :out "2007-03-10T17:00:00Z"}
           {:in  "03-DEC-2007"
            :out "2007-12-03T17:00:00Z"}
           {:in  "10-APR-2007"
            :out "2007-04-10T16:00:00Z"}
           {:in  "01-SEP-2007"
            :out "2007-09-01T16:00:00Z"}
           {:in  "03-OCT-2007"
            :out "2007-10-03T16:00:00Z"}
           {:in  "20010704000000.000000"
            :out "2001-07-04T04:00:00Z"}
           {:in  "20010704072315.321000"
            :out "2001-07-04T11:23:15.321Z"}]]
    (t/is (= out (str (sut/parse-instant in))))))
