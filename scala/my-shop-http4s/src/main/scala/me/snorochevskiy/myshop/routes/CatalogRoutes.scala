package me.snorochevskiy.myshop.routes

import cats.data.{EitherT, Kleisli, OptionT}
import cats.effect.Async
import cats.syntax.all.*
import fs2.Stream
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.Encoder.AsArray.importedAsArrayEncoder
import me.snorochevskiy.myshop.auth.{AuthHelper, UserSession}
import me.snorochevskiy.myshop.model.*
import me.snorochevskiy.myshop.repo.CatalogRepo
import me.snorochevskiy.myshop.service.{CatalogService, CustomerService}
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.headers.*
import org.http4s.circe.*
import org.http4s.server.AuthMiddleware

class CatalogRoutes[F[_] : Async](
  catalogService: CatalogService[F],
) extends Http4sDsl[F]:


  private val publicRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "api" / "products" / LongVar(id) =>
      catalogService.getProduct(id).flatMap {
        case Left(_) => NotFound()
        case Right(list) => Ok(list.asJson)
      }

    case GET -> Root / "api" / "products" / LongVar(id) / "breadcrumbs" =>
      catalogService.buildProductBreadcrumbs(id).flatMap {
        case Left(_) => NotFound()
        case Right(breadCrumbs: List[Long]) => Ok(breadCrumbs.asJson)
      }
  }

  val routes = publicRoutes
