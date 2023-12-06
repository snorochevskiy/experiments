package me.snorochevskiy.myshop.db

import cats.effect.{Async, IO, Resource}
import doobie.hikari.HikariTransactor
import me.snorochevskiy.myshop.config.DatabaseConfig
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

object Database {
  def transactor[F[_] : Async](config: DatabaseConfig, executionContext: ExecutionContext): Resource[F, HikariTransactor[F]] =
    HikariTransactor.newHikariTransactor[F](
      config.driver,
      config.url,
      config.user,
      config.password,
      executionContext
    )

  def initialize[F[_] : Async](transactor: HikariTransactor[F]): F[Unit] =
    transactor.configure { dataSource =>
      Async[F].delay {
        val flyWay = Flyway.configure().dataSource(dataSource).load()
        flyWay.migrate()
        ()
      }
    }
}
