(ns ecommerce.model)

;in Java
;java.util.UUID uuid = java.util.UUID.randomUUID();

(defn uuid []
  (java.util.UUID/randomUUID))

(defn new-product
  ([uuid, name, path, price]
    {:product/id    uuid
     :product/name  name
     :product/path  path
     :product/price price})
  ([name, path, price]
    (new-product (uuid) name path price)))

;(new-product "Chair" "/chair" 100M)
;(new-product #uuid"165fda68-14d2-474b-85f2-1dc8103abe5a" "Sofa" "/sofa" 1000M)

(defn new-category
  ([name] (new-category (uuid) name))
  ([uuid name] {:category/id uuid, :category/name name}))

;(new-category "Sports")
;(new-category #uuid"ea05aa44-7464-4ce8-bb51-7158257e6fa1" "House")
