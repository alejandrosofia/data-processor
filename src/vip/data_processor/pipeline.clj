(ns vip.data-processor.pipeline
  (:require [clojure.tools.logging :as log]
            [clojure.stacktrace :as stacktrace]))

(defn try-processing-fn
  "Attempt to run the processing function on the context. If the
  processing function throws, add a `:stop` key and the `Throwable` to
  the context."
  [processing-fn ctx]
  (try
    (processing-fn ctx)
    (catch Throwable e
      (log/error e)
      (assoc ctx :stop "Exception caught"
                 :thrown-by processing-fn
                 :exception e))))

(defn run-pipeline
  "Run the `pipeline` attached to a processing context. Will return
  the context after all processing functions in the pipeline have
  been completed or until a `:stop` key is added to the context."
  [c]
  (loop [ctx c]
    (let [pipeline (:pipeline ctx)]
      (if-let [next-step (first pipeline)]
        (let [ctx-with-rest-pipeline (assoc ctx :pipeline (rest pipeline))
              next-ctx (try-processing-fn next-step ctx-with-rest-pipeline)]
          (if (:stop next-ctx)
            next-ctx
            (recur next-ctx)))
        ctx))))

(defn process
  "A pipeline is a sequence of functions that take and return a
  `processing context`. The `initial-input` will be placed as the
  `:input` on the processing context for the first function in the
  pipeline.

  Runs the pipeline, returning the final context. An exception on the
  context will result in logging the exception, and this function
  throwing it."
  [pipeline initial-input]
  (let [ctx {:input initial-input
             :warnings {}
             :errors {}
             :pipeline pipeline}
        result (run-pipeline ctx)]
    (log/info result)
    (when-let [ex (:exception result)]
      (log/error (with-out-str (stacktrace/print-stack-trace ex)))
      (throw (ex-info "Exception during processing" {:exception ex
                                                     :initial-ctx ctx
                                                     :final-ctx result})))
    result))