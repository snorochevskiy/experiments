package me.snorochevskiy.myshop

import cats.MonadThrow
import cats.data.Kleisli
import cats.effect.{Async, ExitCode, Resource}
import cats.syntax.all.*
import com.comcast.ip4s.*
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import me.snorochevskiy.myshop.auth.SessionHolder
import me.snorochevskiy.myshop.config.Config
import me.snorochevskiy.myshop.db.Database
import me.snorochevskiy.myshop.repo.{CatalogRepoImpl, CustomerRepo, CustomerRepoImpl}
import me.snorochevskiy.myshop.service.{CatalogService, CustomerService}
import me.snorochevskiy.myshop.routes.{AccountRoutes, CatalogRoutes}
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{ErrorHandling, Logger}

object MyShopServer:

  def create[F[_] : Async](configFile: String = "application.conf"): F[ExitCode] =
    resources(configFile)
      .use(create)

  private def resources[F[_]: Async](configFile: String): Resource[F, AppResources[F]] =
    for {
      config <- Config.load(configFile)
      ec <- ExecutionContexts.fixedThreadPool[F](config.database.threadPoolSize)
      transactor <- Database.transactor(config.database, ec)
    } yield AppResources(transactor, config)

  private def create[F[_]: Async](resources: AppResources[F]): F[ExitCode] =
    import org.http4s.server.middleware.ErrorAction
    import org.http4s.server.middleware.ErrorHandling
    for {
      _ <- Database.initialize(resources.transactor)
      appRoutes = errWrappedRoutes(
        buildRoutes(resources.transactor).orNotFound
      )
      exitCode <- EmberServerBuilder.default[F]
        .withHost(Host.fromString(resources.config.server.host).get)
        .withPort(Port.fromInt(resources.config.server.port).get)
        .withHttpApp(appRoutes)
        .build
        .use(_ => Async[F].never)
        .as(ExitCode.Success)
    } yield exitCode

  def buildRoutes[F[_]: Async](transactor: HikariTransactor[F]): HttpRoutes[F] = {
    val catalogRepo = new CatalogRepoImpl(transactor)
    val customerRepo = new CustomerRepoImpl(transactor)

    val catalogService = new CatalogService(catalogRepo)
    val customerService = new CustomerService(customerRepo)

    val sessionHolder = new SessionHolder()

    val catalogRoutes = new CatalogRoutes(catalogService)
    val accountRoutes = new AccountRoutes(customerService, sessionHolder)

    accountRoutes.routes <+> catalogRoutes.routes
  }

  def errWrappedRoutes[F[_] : Async, G[_]](k: Kleisli[F, Request[G], Response[G]]) =
    import org.http4s.server.middleware.ErrorAction
    import org.http4s.server.middleware.ErrorHandling
    ErrorHandling(
      ErrorAction.log(
        k,
        messageFailureLogAction = (t, msg) =>
          Async[F].blocking(println(msg)) >>
            Async[F].blocking(println(t)),
        serviceErrorLogAction = (t, msg) =>
          Async[F].blocking(println(msg)) >>
            Async[F].blocking(println(t))
      )
    )

  case class AppResources[F[_]: Async](transactor: HikariTransactor[F], config: Config)
