package me.snorochevskiy.myshop.routes

import cats.effect.IO
import cats.effect.syntax.all.*
import cats.effect.unsafe.IORuntime
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import cats.syntax.option.*
import cats.syntax.applicative.*
import io.circe.Json
import io.circe.literal._
import org.http4s.{Request, Status}
import org.http4s.dsl.io.GET
import me.snorochevskiy.myshop.service.CatalogService
import me.snorochevskiy.myshop.model.{Category, CategoryNotFoundError, EntityNonFound, Product, ProductNotFoundError}
import me.snorochevskiy.myshop.repo.{CatalogRepo, CatalogRepoImpl}

import scala.collection.mutable

class CatalogRoutesTest extends AnyWordSpec with Matchers {

  class TestCatalogRepo(
                         val products: mutable.HashSet[Product] = mutable.HashSet(),
                         val categories: mutable.HashSet[Category] = mutable.HashSet()
                       ) extends CatalogRepo[IO]:
    def selectProductById(id: Long): IO[Either[EntityNonFound, Product]] =
      products.find(_.id.contains(id))
        .toRight[EntityNonFound](ProductNotFoundError)
        .pure

    def selectProductInCategory(categoryId: Long): fs2.Stream[IO, Product] =
      fs2.Stream.apply[IO, Product](products.filter(_.categoryId == categoryId).toSeq: _*)

    def selectCategoryById(id: Long): IO[Either[EntityNonFound, Category]] =
      categories.find(_.id.contains(id))
        .toRight[EntityNonFound](CategoryNotFoundError)
        .pure

  private given runtime: IORuntime = cats.effect.unsafe.IORuntime.global

  "Product controller" must {
    "provide product by id" in {
      val repository = new TestCatalogRepo()
      val service = new CatalogService[IO](repository)
      val routes = new CatalogRoutes[IO](service).routes

      repository.products.add(Product(1L.some, "Product_1", "Description_1", 1L))

      val response = routes.orNotFound(Request[IO](GET, uri"/api/products/1")).unsafeRunSync()
      response.status mustBe Status.Ok
      response.as[Json].unsafeRunSync() mustBe
        json"""
              {
                "id": 1,
                "title": "Product_1",
                "description": "Description_1",
                "categoryId": 1
              }"""
    }
  }
}
