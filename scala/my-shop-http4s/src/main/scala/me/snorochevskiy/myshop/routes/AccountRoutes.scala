package me.snorochevskiy.myshop.routes

import cats.data.OptionT
import cats.effect.Async
import cats.syntax.all.*
import me.snorochevskiy.myshop.auth.{AuthHelper, SessionHolder, UserSession}
import me.snorochevskiy.myshop.service.{CatalogService, CustomerService}
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto.*
import io.circe.syntax.*
import me.snorochevskiy.myshop.model.Customer
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.headers.*
import org.http4s.{HttpRoutes, MediaType, Request, Response, Status, Uri}
import org.http4s.circe.*
import org.http4s.server.AuthMiddleware

case class LoginCreds(login: String, password: String)

class AccountRoutes[F[_] : Async](
  customerService: CustomerService[F],
  implicit val sessionHolder: SessionHolder[F]
) extends Http4sDsl[F]:

  implicit val credsDecoder: EntityDecoder[F, LoginCreds] = jsonOf[F, LoginCreds]

  private val publicRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@POST -> Root / "api" / "login" =>
      for {
        creds <- req.as[LoginCreds]
        response <- signIn(creds).flatMap {
          case None =>
            Forbidden()
          case Some(session) =>
            sessionHolder.create(session).flatMap(sessionId =>
              Ok("Logged in").map(_.addCookie(ResponseCookie("sessionid", sessionId)))
            )
        }
      } yield response
  }

  private val protectedRouts: AuthedRoutes[UserSession, F] =
    AuthedRoutes.of {
      case GET -> Root / "test" / "welcome" as user =>
        Ok(s"Welcome, ${user.name}")
    }

  val routes = publicRoutes <+> AuthHelper[F]().middleware(protectedRouts)

  def signIn(creds: LoginCreds): F[Option[UserSession]] =
    creds match
      case LoginCreds(login, password) =>
        OptionT(customerService.loginCustomer(login, password))
          .map { case Customer(id, login, passwd, passwdType, title) => UserSession(id.get, title) }
          .value