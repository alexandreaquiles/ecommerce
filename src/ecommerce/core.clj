(ns ecommerce.core
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))

;:db.error/nil-value Nil is not a legal value
;:db.error/not-an-entity Unable to resolve entity: :product/patttt
;(let [radio {:product/name "Radio", :product/path nil} ]
;  (pprint radio)
;  (pprint (d/transact conn [radio])))

;(let [tv (model/new-product "TV" "/tv" 33000.33M)
;      result @(d/transact conn [tv])
;      entity-id (-> result :tempids vals first)]
;  (pprint result)
;  (pprint entity-id)
;  (pprint @(d/transact conn [[:db/retract entity-id :product/path "/tv"]])))
;dereferencing a future (waiting for a future to be completed)
;future.get(); //blocks and waits

;[#datom[13194139534334 50 #inst "2020-05-27T20:46:11.812-00:00" 13194139534334 true]
; #datom[17592186045437 73 "/tv" 13194139534334 false]],


;(let [vhs (model/new-product "VHS" "/vhs" 100.00M)
;      result @(d/transact conn [vhs])
;      entity-id (-> result :tempids vals first)]
;  (pprint @(d/transact conn [[:db/add entity-id :product/price 10.50M]])))

;#datom[entity-id      attribute     value    tx-id           added/retracted?
;#datom[17592186045434  74          10.50M    13194139534331  true]
;#datom[17592186045434  74          100.00M   13194139534331  false]


;{:db-before datomic.db.Db@2e5035ae,
; :db-after datomic.db.Db@57efba29,
; :tx-data [#datom[13194139534321 50 #inst "2020-05-27T20:31:08.427-00:00" 13194139534321 true] #datom[17592186045426 72 "VHS" 13194139534321 true] #datom[17592186045426 73 "/vhs" 13194139534321 true] #datom[17592186045426 74 100.00M 13194139534321 true]],
; :tempids {-9223301668109598122 17592186045426}}

;{-9223301668109598122 17592186045426}
;(17592186045426)
;17592186045426

;snapshot of the database at a give time
;(def db (d/db conn))
;(pprint db)

;(let [computer (model/new-product "Computer" "/computer" 2500.10M)
;      phone (model/new-product "Phone" "/phone" 888.88M)
;      calculator {:product/name "Calculator"}
;      tv (model/new-product "TV" "/tv" 33000.33M)
;      vhs (model/new-product "VHS" "/vhs" nil)]
;  (pprint @(d/transact conn [computer phone calculator tv vhs])))
;ATOMIC operation
;Execution error (Exceptions$IllegalArgumentExceptionInfo) at datomic.error/arg (error.clj:79).
;:db.error/nil-value Nil is not a legal value
;(pprint (db/entity-ids-of-every-product (d/db conn)))
;#{}
;=> nil

;(pprint (db/entity-ids-of-every-product (d/db conn)))
;
;(pprint (db/entity-id-of-a-product-by-path (d/db conn) "/computer"))
;(pprint (db/entity-id-of-a-product-by-path (d/db conn) "/tv"))
;(pprint (db/entity-id-of-a-product-by-path (d/db conn) "/doesnt-exist"))
;
;(pprint (db/all-paths (d/db conn)))
;
;(pprint (db/product-names-and-prices (d/db conn)))
;
;(pprint (db/entity-id-of-a-product-by-name-and-price (d/db conn) "Computer" 2500.10M))
;(db/entity-id-of-a-product-by-name-and-price (d/db conn) "Computer" 2500.10M)

;(def hello-from-the-past (d/db conn))
;(println "hello-from-the-past:" hello-from-the-past)

;(pprint (db/all-products (d/db conn)))
;(pprint (count (db/all-products (d/db conn))))

;(pprint (db/all-products (d/db conn)))
;(pprint (count (db/all-products (d/db conn))))
;
;(pprint "-------------------------------")
;
;(pprint (db/all-products hello-from-the-past))
;(pprint (count (db/all-products hello-from-the-past)))

;(count (db/all-products (d/as-of (d/db conn) #inst "2019-05-28T19:55:26.776-00:00")))
;(count (db/all-products (d/as-of (d/db conn) #inst "2020-05-28T19:55:26.776-00:00")))
;(count (db/all-products (d/as-of (d/db conn) #inst "2020-05-28T20:00:26.776-00:00")))
;(count (db/all-products (d/as-of (d/db conn) #inst "2021-05-28T20:00:26.776-00:00")))

;(db/all-paths (d/as-of (d/db conn) #inst "2021-05-28T19:55:26.776-00:00"))

;AUDITING and DEBUGGING with data from the past and MACHINE LEARNING!!!
;DDD - Event Sourcing (store the Domain Events the happened to an Aggregate)
;To build machine learning models!

;(def conn (db/connect-to-db))
;(pprint conn)
;
;(pprint @(d/transact conn db/schema))
;
;(let [computer (model/new-product "Computer" "/computer" 2500.10M)
;      phone (model/new-product "Phone" "/phone" 888.88M)]
;  (pprint @(d/transact conn [computer phone])))
;
;(let [calculator {:product/name "Calculator"}
;      tv (model/new-product "TV" "/tv" 33000.33M)]
;  (pprint @(d/transact conn [calculator tv])))


;(def at-5pm (d/as-of (d/db conn) #inst "2020-05-28T20:00:26.776-00:00"))
;(count (db/all-products at-5pm))
;
;(db/all-products-with-price-greater-than at-5pm 1000M)

;FILTER

;10k products with prices and quantities

;the products with prices > 1000M and quantities < 5

; ---> 5k products with prices > 1000M
; ---> 150 products only with quantities < 5

;WHICH IS FASTER?

;A) prices > 1000M AND THEN quantities < 5
;10k
; [(> prices 1000M)] ====> 5k
;5k
; [(< quantities 5)] =====> 10
;return those 10 products

;B) quantities < 5 AND THEN prices > 1000M
;10k
; [(< quantities 5)] =====> 150
;150
; [(> prices 1000M)] ====> 10
;return those 10 products

;(def conn (db/connect-to-db))
;
;@(d/transact conn db/schema)
;
;(let [computer (model/new-product (model/uuid) "Computer" "/computer" 2500.10M)
;      phone (model/new-product "Phone" "/phone" 888.88M)
;      calculator {:product/name "Calculator"}
;      tv (model/new-product "TV" "/tv" 33000.33M)]
;      @(d/transact conn [computer phone calculator tv]))
;
;;Computer
;(d/transact conn [[:db/add 17592186045418 :product/tag "desktop"]
;                  [:db/add 17592186045418 :product/tag "appliances"]])
;
;;(d/transact conn [[:db/retract 17592186045418 :product/tag "desktop"]])
;
;;Phone
;(d/transact conn [[:db/add 17592186045419 :product/tag "handheld"]])
;
;;TV
;(d/transact conn [[:db/add 17592186045421 :product/tag "appliances"]])
;
;
;(pprint (db/all-products-by-tag (d/db conn) "appliances"))
;
;(def product-ids (db/entity-ids-of-every-product (d/db conn)))
;(pprint product-ids)
;;(def first-product-id (first (first product-ids)))
;(def first-product-id (ffirst product-ids) )
;(pprint first-product-id)
;(pprint (db/product-by-id (d/db conn) first-product-id))
;(pprint (db/product-by-id (d/db conn) 17592186045418))      ;Computer
;
;(pprint (db/product-by-uuid (d/db conn) #uuid"e9a471e5-3ec9-4740-a4ba-b8b56a0ff3c8"))
;
;(pprint (db/product-by-name (d/db conn) "Computer"))
;
;(pprint (db/all-products (d/db conn)))
;
;;a UUID (unique/identity) that already exists
;;it will do adds/retracts
;(let [changed-computer (model/new-product
;                          #uuid"e9a471e5-3ec9-4740-a4ba-b8b56a0ff3c8"
;                          "Changed computer" "/chaged-computer" 5000M)]
;  (pprint @(d/transact conn [changed-computer])))
;
;(pprint (db/product-by-uuid (d/db conn) #uuid"e9a471e5-3ec9-4740-a4ba-b8b56a0ff3c8"))
;
;@(d/transact conn db/schema)
;
;(def sports (model/new-category "Sports"))
;(def eletronics (model/new-category "Eletronics"))
;@(d/transact conn [sports eletronics])
;
;(def chess (model/new-product "Chess" "/chess" 30M))
;@(d/transact conn [chess])
;
;(db/all-categories (d/db conn))
;
;(pprint (db/all-products (d/db conn)))
;
;@(d/transact conn db/schema)
;
;;used entity ids (:db/id) to associate Computer with category Eletronics
;@(d/transact conn [[:db/add 17592186045418 :product/category 17592186045429]])
;
;;trying to use UUIDs to associate Chess with Sports (DIDN'T WORK)
;;@(d/transact conn [[:db/add #uuid"53be0475-17a9-4789-93ad-cd1b87441c0c"
;;                            :product/category
;;                            #uuid"c7df4088-b97d-4552-94d7-91b4b1aae35e"]])
;
;;using UUIDs with Lookup Refs to associate Chess with Sports
;@(d/transact conn [[:db/add
;                    [:product/id #uuid"53be0475-17a9-4789-93ad-cd1b87441c0c"]
;                    :product/category
;                    [:category/id #uuid"c7df4088-b97d-4552-94d7-91b4b1aae35e"]]])
;
;
;(def computer (db/product-by-id (d/db conn) 17592186045418))      ;Computer
;(def phone (db/product-by-id (d/db conn) 17592186045419))      ;Computer
;(def tv (db/product-by-id (d/db conn) 17592186045421))      ;Computer
;
;;generate those :db/adds (vector of :db/adds)
;
;(db/associate-category-to-products conn [computer phone tv] eletronics)
;(db/associate-category-to-products conn [chess] sports)
;
;(def ball (model/new-product "Ball" "/ball" 10M))
;(d/transact conn [ball])
;
;(db/associate-category-to-products conn [ball] sports)

;(db/erase-db)

(def conn (db/connect-to-db!))

(db/create-schema! conn)

(let [computer (model/new-product (model/uuid) "Computer" "/computer" 2500.10M)
      phone (model/new-product "Phone" "/phone" 888.88M)
      calculator {:product/name "Calculator"}
      tv (model/new-product "TV" "/tv" 33000.33M)
      chess (model/new-product "Chess" "/chess" 30M)
      ball (model/new-product "Ball" "/ball" 10M)
      sports (model/new-category "Sports")
      eletronics (model/new-category "Eletronics")]
      (db/add-products! conn [computer phone calculator tv chess ball])
      (db/add-categories! conn [eletronics sports])
      (db/associate-category-to-products! conn [computer phone tv] eletronics)
      (db/associate-category-to-products! conn [chess ball] sports))

(pprint (db/all-categories (d/db conn)))
(pprint (db/all-products (d/db conn)))

;(db/erase-db!)
