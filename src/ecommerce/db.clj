(ns ecommerce.db
  (:require [datomic.api :as d]))

(def db-uri "datomic:dev://localhost:4334/ecommerce")

(defn connect-to-db! []
  (d/create-database db-uri)
  (d/connect db-uri))

(defn erase-db! []
  (d/delete-database db-uri))

;there are NO TABLES
;datom
;entity id           attribute                  value         tx id     add?
;17...18             :product/name (72)        "Computer"
;17...18             :product/path (73)        "/computer"
;17...18             :product/price (74)       2500.10
;#datom[17592186045418 72 "Computer" 13194139534313 true]
;#datom[17592186045418 73 "/computer" 13194139534313 true]
;#datom[17592186045418 74 2500.10M 13194139534313 true]]
;
;19...21             :product/name        "Phone"
;19...21             :product/path        "/phone"
;19...21             :product/price       888.88








;#datom[72 10 :product/name 13194139534312 true]
;#datom[72 40 23 13194139534312 true]
;#datom[72 41 35 13194139534312 true]
;#datom[72 62 "Name of the product" 13194139534312 true]

;#datom
;[entity-id attribute value tx-id add?]

;sequential id
;exploit trying to guess the next id

;UUID (Universal Unique IDentifier)
;128 bits hexadecimal with -

;:db/id Datomic ID
;another id attribute for an application id (UUID)

(def schema [{:db/ident       :product/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/doc         "UUID of the product"
              :db/unique      :db.unique/identity}

             {:db/ident       :product/name                 ;:db/ident 10
              :db/valueType   :db.type/string               ;:db/valueType 40
              :db/cardinality :db.cardinality/one
              :db/doc         "Name of the product"}

             {:db/ident       :product/path
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "Path of the product"}

             {:db/ident       :product/price
              :db/valueType   :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc         "Path of the product"}

             {:db/ident       :product/tag
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/many
              :db/doc         "Tags of the product"}

             {:db/ident       :product/category
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/one
              :db/doc         "Category of the product"}

             {:db/ident       :category/name
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "Name of a category"}

             {:db/ident       :category/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/doc          "UUID of the category"
              :db/unique      :db.unique/identity}
             ])



;Datalog Query
(defn entity-ids-of-every-product [db]
  (d/q '[:find ?e
         ;:in $ (IMPLICIT)
         :where [?e :product/name]] db))

(defn entity-id-of-a-product-by-path [db path-argument]
  (d/q '[:find ?e
         :in $ ?path
         :where [?e :product/path ?path]] db path-argument))

(defn all-paths [db]
  (d/q '[:find ?path
         :where [_ :product/path ?path]] db))

(defn product-names-and-prices [db]
  (d/q '[:find ?name ?price
         :keys product/name, product/price
         :where [?e :product/price ?price]
                [?e :product/name ?name]] db))


(defn entity-id-of-a-product-by-name-and-price [db name-argument price-argument]
  (d/q '[:find ?e
         :in $ ?name ?price
         :where [?e :product/price ?price]
                [?e :product/name ?name]] db name-argument price-argument))

;(defn all-products [db]
;  (d/q '[:find ?e ?name ?price ?path
;         :where  [?e :product/name ?name]
;                 [?e :product/price ?price]
;                 [?e :product/path ?path]] db))
; It only works when products have all attributes

(defn all-products [db]
  (d/q '[:find (pull ?e [*])
         :where [?e :product/name]] db))

(defn all-products-with-price-greater-than [db minimum-price]
  (d/q '[:find ?name ?price
         :in $ ?min-price
         :keys product/name, product/price
         :where [?e :product/price ?price]
                [?e :product/name ?name]
                [(> ?price ?min-price)]] db minimum-price))

(defn all-products-by-tag [db tag-param]
  (d/q '[:find (pull ?e [*])
         :in $ ?tag
         :where [?e :product/tag ?tag]] db tag-param)
  )

(defn product-by-id [db product-id]
  (d/pull db '[*] product-id))

(defn product-by-uuid [db product-uuid]
  (d/pull db '[*] [:product/id product-uuid]))
                   ;Lookup Ref

(defn product-by-name [db product-name]
  ;won't work
  ;(d/pull db '[*] [:product/name product-name])
  (d/q '[:find (pull ?e [*])
         :in $ ?name
         :where [?e :product/name ?name]] db product-name))

(defn all-categories [db]
  (d/q '[:find (pull ?e [*])
         :where [?e :category/id]] db))

(defn generate-db-adds-to-associate-products-with-category [products category]
  (reduce
    (fn [db-adds product] (conj db-adds [:db/add
                                         [:product/id (:product/id product)]
                                         :product/category
                                         [:category/id (:category/id category)]]))
    [] products))


(defn create-schema! [conn]
  (d/transact conn schema))

(defn associate-category-to-products! [conn products category]
  (let [db-adds-to-transact (generate-db-adds-to-associate-products-with-category products category)]
    (d/transact conn db-adds-to-transact)))

(defn add-products! [conn products]
  (d/transact conn products))

(defn add-categories! [conn categories]
  (d/transact conn categories))
