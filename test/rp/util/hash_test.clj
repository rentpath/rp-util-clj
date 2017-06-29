(ns rp.util.hash-test
  (:require [clojure.test :refer :all]
            [rp.util.hash :refer :all]))

(deftest md5-test
  (is (= "9f9d51bc70ef21ca5c14f307980a29d8"
         (md5 "bob"))))
