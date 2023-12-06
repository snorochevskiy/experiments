package me.snorochevskiy.myshop.model

case class Category(
                     id: Option[Long],
                     title: String,
                     description: String,
                     parentId: Option[Long]
                   )

case class Product(
                    id: Option[Long],
                    title: String,
                    description: String,
                    categoryId: Long
                  )

trait EntityNonFound
case object CategoryNotFoundError extends EntityNonFound
case object ProductNotFoundError extends EntityNonFound

case class Sku(
              id: Option[Long],
              productId: Long,
              title: String,
              price: BigDecimal,
              inStock: Int
              )

case class ProductImages(
                        id: Option[Long],
                        skuId: Long,
                        url1: String,
                        url2: String,
                        url3: String,
                        url4: String,
                        url5: String,
                        url6: String,
                        url7: String,
                        url8: String,
                        url9: String,
                        url10: String
                        )

case class Customer(
                   id: Option[Long],
                   login: String,
                   passwd: Array[Byte],
                   passwdType: String,
                   title: String
                   )

case class Address(
                  id: Option[Long],
                  customerId: Long,
                  addressInfo: String
                  )

case class Checkout(
                   id: Option[Long],
                   customerId: Long,
                   createdDate: java.sql.Date,
                   createdTime: java.sql.Time,
                   addressId: Long,
                   status: Int
                   )

case class ItemInCart(
                     id: Option[Long],
                     checkoutId: Long,
                     skuId: Long,
                     quantity: Long
                     )
