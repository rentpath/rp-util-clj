(ns rp.util.time-test
  (:require [rp.util.time :as sut]
            [clojure.test :as t]))

(defn test-parse-instant
  [instant s message]
  (t/is (= instant (str (sut/parse-instant s)))
        message))

(t/deftest test-parse-invalid-input
  (t/testing "invalid input"
    (t/is (not (sut/parse-instant "foo")))))

(t/deftest test-parse-iso-instant
  (test-parse-instant "2007-12-03T10:15:30Z"
                      "2007-12-03T10:15:30Z"
                      "should parse as iso instant"))

(t/deftest test-last-update-format
  (t/testing "Standard Time"
    (test-parse-instant "2007-12-03T15:15:30Z"
                        "12/03/2007 10:15:30"
                        "should parse as standard time iso instant"))
  (t/testing "Daylight Savings Time"
    (test-parse-instant "2007-08-03T14:15:30Z"
                        "08/03/2007 10:15:30"
                        "should parse as daylight savings time iso instant")))

(t/deftest test-review-format
  (t/testing "Standard Time"
    (doseq [time-tuple [["2007-03-01T17:00:00Z" "01-MAR-2007"]
                        ["2007-03-10T17:00:00Z" "10-MAR-2007"]
                        ["2007-12-03T17:00:00Z" "03-DEC-2007"]]]
      (let [args (conj time-tuple "should parse as a standard time iso instant")]
        (apply test-parse-instant args))))
  (t/testing "Daylight Savings Time"
    (doseq [time-tuple [["2007-04-10T16:00:00Z" "10-APR-2007"]
                        ["2007-09-01T16:00:00Z" "01-SEP-2007"]
                        ["2007-10-03T16:00:00Z" "03-OCT-2007"]]]
      (let [args (conj time-tuple "should parse as a daylight savings time iso instant")]
        (apply test-parse-instant args)))))

(t/deftest test-terminate-format
  (test-parse-instant "2001-07-04T04:00:00Z"
                      "20010704000000.000000"
                      "should parse terminate format (with zero time)")
  (test-parse-instant "2001-07-04T11:23:15.321Z"
                      "20010704072315.321000"
                      "should parse terminate format (with non-zero time)"))
