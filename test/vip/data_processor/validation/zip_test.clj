(ns vip.data-processor.validation.zip-test
  (:require
   [clojure.java.io :as io]
   [clojure.test :refer [deftest testing is use-fixtures run-tests]]
   [vip.data-processor.validation.zip :as zip])
  (:import
   [net.lingala.zip4j.core ZipFile]))

(deftest wont-open-too-big-zipfile
  (let [zipfile-path (.getPath (io/resource "example.zip"))
        uncompressed-size (-> (ZipFile. zipfile-path)
                              zip/get-uncompressed-size)
        max-zipfile-size 786432]
    (testing "Don't proceed if the zipfile is bigger than the `max-zipfile-size'"
      (is (= {:input zipfile-path
              :stop (zip/too-big-msg uncompressed-size max-zipfile-size)}
             (zip/assoc-file {:input zipfile-path} max-zipfile-size))))

    (testing "Doesn't fail if max-zipfile-size is passed in as a string"
      (is (= {:input zipfile-path
              :stop (zip/too-big-msg uncompressed-size (str max-zipfile-size))}
             (zip/assoc-file {:input zipfile-path} (str max-zipfile-size)))))))
