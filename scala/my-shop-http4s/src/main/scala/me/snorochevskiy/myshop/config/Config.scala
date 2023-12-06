package me.snorochevskiy.myshop.config

import cats.effect.{Async, IO, Resource}
import com.typesafe.config.ConfigFactory
import pureconfig.*
import pureconfig.generic.derivation.*
import pureconfig.module.catseffect.syntax.*


case class ServerConfig(host: String, port: Int)

case class DatabaseConfig(driver: String, url: String, user: String, password: String, threadPoolSize: Int)

case class Config(server: ServerConfig, database: DatabaseConfig)

object Config:
  import pureconfig.generic.derivation.default.derived
  given cfgReader: ConfigReader[Config] = ConfigReader.derived[Config]

  def load[F[_] : Async](configFile: String = "application.conf"): Resource[F, Config] =
    Resource.eval(
      ConfigSource.fromConfig(ConfigFactory.load(configFile)).loadF[F, Config]()
    )
