(ns dev.load
  (:require [dev.core :as core]
            [vip.data-processor
             [pipeline :as pipeline]
             [s3 :refer [zip-filename]]]
            [vip.data-processor.db.postgres :as psql]
            [vip.data-processor.validation.zip :as zip]))

(defn -main [filename]
  (psql/initialize)
  (let [file (if (zip/xml-file? filename)
               (java.nio.file.Paths/get filename (into-array [""]))
               (java.io.File. filename))
        result (pipeline/process core/pipeline file
                                 {:skip-validations? true
                                  :skip-summary? true})]
    (psql/fail-run (:import-id result) "Loaded data, nothing more")))
