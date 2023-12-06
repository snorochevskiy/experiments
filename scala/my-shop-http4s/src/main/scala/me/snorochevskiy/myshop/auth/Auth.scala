package me.snorochevskiy.myshop.auth

import cats.{MonadError, MonadThrow}
import cats.data.{EitherT, Kleisli, OptionT}
import cats.effect.{Async, IO, LiftIO}
import cats.syntax.all.*
import cats.effect.syntax.all.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.headers.*
import org.http4s.circe.*
import org.http4s.server.AuthMiddleware
import org.http4s.syntax.AllSyntax
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger

case class AuthError(msg: String)

class AuthHelper[F[_] : Async](sessionHolder: SessionHolder[F]) {

  implicit def unsafeLogger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]

  object dsl extends Http4sDsl[IO] {}
  import dsl._

  import AuthHelper.*

  def middleware: AuthMiddleware[F, UserSession] =
    AuthMiddleware(authUserCookie, onAuthFailure)

  private def onAuthFailure: AuthedRoutes[AuthError, F] = Kleisli { (req: AuthedRequest[F, AuthError]) =>
    OptionT.liftF(
      Async[F].pure(
        Response[F](
          status = Status.Unauthorized
        )
      )
    )
  }

  private def retrieveUser: Kleisli[F, String, Option[UserSession]] = Kleisli(sessionId =>
    sessionHolder.find(sessionId)
  )

  private[this] def authUserCookie: Kleisli[F, Request[F], Either[AuthError, UserSession]] = Kleisli{ request =>
    for {
      header <- request.headers.get[Cookie]
          .toRight(AuthError("Cookie parsing error"))
          .pure[F]
      cookie <- header.flatMap(_.values.toList.find(_.name == SessionCookieName)
        .toRight(AuthError(s"Couldn't find the $SessionCookieName")))
        .pure[F]
      _ <- Logger[F].info(s"Cookie: $cookie")
      session <- cookie.map(_.content).traverse(retrieveUser.run)
        .map(_.flatMap(_.toRight(AuthError("Cannot find session for given sessionId"))))
    } yield session
  }

  // A version which is better, but without looging.
  // Keeping it just to remember that log4cats forces you to write code in a compatible for logging way
  def authUserCookie_old: Kleisli[F, Request[F], Either[AuthError, UserSession]] = Kleisli({ request =>
    val sessionId =
      for {
        header <- request.headers.get[Cookie]
          .toRight(AuthError("Cookie parsing error"))
        cookie <- header.values.toList.find(_.name == SessionCookieName)
          .toRight(AuthError(s"Couldn't find the $SessionCookieName"))
        sessionId = cookie.content
      } yield sessionId

    val session = sessionId.traverse(retrieveUser.run)
      .map(_.flatMap(_.toRight(AuthError("Cannot find session for given sessionId"))))

    session
  })

}

object AuthHelper {
  val SessionCookieName = "sessionid"

  def apply[F[_] : Async]()(implicit sessionHolder: SessionHolder[F]) = new AuthHelper(sessionHolder)
}