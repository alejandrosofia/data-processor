(ns vip.data-processor.validation.v5.polling-location-test
  (:require [vip.data-processor.validation.v5.polling-location :as v5.polling-location]
            [clojure.test :refer :all]
            [vip.data-processor.test-helpers :refer :all]
            [vip.data-processor.db.postgres :as psql]
            [vip.data-processor.validation.xml :as xml]))

(use-fixtures :once setup-postgres)
(use-fixtures :each with-clean-postgres)

(deftest ^:postgres validate-no-missing-address-lines-test
  (let [ctx {:input (xml-input "v5-polling-locations.xml")}
        out-ctx (-> ctx
                    psql/start-run
                    xml/load-xml-ltree
                    v5.polling-location/validate-no-missing-address-lines)]
    (testing "address-line missing is an error"
      (is (get-in out-ctx [:errors :polling-location
                           "VipObject.0.PollingLocation.0.AddressLine"
                           :missing])))
    (testing "address-line present is OK"
      (is (not (get-in out-ctx [:errors :polling-location
                                "VipObject.0.PollingLocation.1.AddressLine"
                                :missing]))))))
