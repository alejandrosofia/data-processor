(ns vip.data-processor.validation.db.record-limit
  (:require [korma.core :as korma]))

(def single-record-files
  "A list of those files which are allowed only a single record."
  {:elections "election.txt"
   :sources "source.txt"
   :states "state.txt"})

(defn count-rows [table]
  "Takes a single table and counts the number of rows in it."
  (:cnt (first (korma/select table (korma/aggregate (count "*") :cnt)))))

(defn file-and-count [tables]
  "Transforms the selected tables into a map of files and counts."
  (map 
   (fn [[_ table]]
     {:file (-> table :table keyword single-record-files)
      :count (count-rows table)})
   tables))

(defn error-if-not-one-row [ctx count]
  "If the count of the rows is not equal to 1, add an error."
  (if-not (= 1 (:count count))
    (assoc-in ctx [:errors (:file count) :row-constraint]
              "File needs to contain exactly one row.")
    ctx))

(defn tables-allow-only-one-record [ctx]
  "Sort through the ctx to select out specific tables as defined
   in single-record-files. Then, count the rows and return errors
   for any that do not have exactly one row."
  (let [table-keys (-> single-record-files keys vec)
        tables (select-keys (:tables ctx) table-keys)
        counts (file-and-count tables)]
    (reduce error-if-not-one-row ctx counts)))


