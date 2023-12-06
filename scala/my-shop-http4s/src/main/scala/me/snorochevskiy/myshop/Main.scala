package me.snorochevskiy.myshop

import cats.effect.{ExitCode, IO, IOApp}
import me.snorochevskiy.myshop.MyShopServer.create

object Main extends IOApp:

  def run(args: List[String]): IO[ExitCode] =
    create[IO]()
