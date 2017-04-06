(ns vip.data-processor.errors.process
  (:require [utility-fns.async :as util-async]
            [vip.data-processor.db.postgres :as psql]
            [vip.data-processor.db.statistics :as stats]
            [vip.data-processor.db.tree-statistics :as tree-stats]))

(defn process-v3-validations [{:keys [errors-chan opts] :as ctx}]
  (let [wrapup-fn (if (true? (:skip-validations opts))
                    (constantly nil)
                    (partial stats/store-stats ctx))
        processing-chan (util-async/batch-process
                         errors-chan
                         (fn [errors]
                           (psql/bulk-import
                            ctx
                            psql/validations
                            (map psql/validation-value errors)))
                         {:batch-size 10000
                          :timeout 5000
                          :pool-size 30
                          :wrapup-f wrapup-fn})]
    (assoc ctx :processing-chan processing-chan)))

(defn process-v5-validations [{:keys [errors-chan opts] :as ctx}]
  (let [wrapup-fn (if (true? (:skip-validations? opts))
                    (constantly nil)
                    (partial tree-stats/store-tree-stats ctx))
        processing-chan (util-async/batch-process
                         errors-chan
                         (fn [errors]
                           (psql/bulk-import
                            ctx
                            psql/xml-tree-validations
                            (map psql/xml-tree-validation-value errors)))
                         {:batch-size 10000
                          :timeout 5000
                          :pool-size 30
                          :wrapup-f wrapup-fn})]
    (assoc ctx :processing-chan processing-chan)))
