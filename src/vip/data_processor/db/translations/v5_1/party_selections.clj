(ns vip.data-processor.db.translations.v5-1.party-selections
  (:require [korma.core :as korma]
            [vip.data-processor.db.postgres :as postgres]
            [vip.data-processor.db.translations.util :as util]))

(defn row-fn [import-id]
  (korma/select (postgres/v5-1-tables :party-selections)
    (korma/where {:results_id import-id})))

(defn base-path [index]
  (str "VipObject.0.PartySelection." index))

(defn transform-fn [idx-fn party-selection]
  (let [party-selection-path (base-path (idx-fn))
        id-path (util/id-path party-selection-path)
        child-idx-fn (util/index-generator 0)]
    (conj
     (mapcat #(% child-idx-fn party-selection-path party-selection)
             [(util/simple-value->ltree :sequence_order)
              (util/simple-value->ltree :party_ids)])
     {:path id-path
      :simple_path (util/path->simple-path id-path)
      :parent_with_id id-path
      :value (:id party-selection)})))

(def transformer (util/transformer row-fn transform-fn))
