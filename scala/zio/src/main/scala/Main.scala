import zio.*
import zio.Console.*

import java.io.IOException

object MyApp extends ZIOAppDefault {

  def run = suspendedEffect

  val myAppLogic =
    for {
      _    <- printLine("Hello! What is your name?")
      name <- readLine
      _    <- printLine(s"Hello, ${name}, welcome to ZIO!")
    } yield ()

  val finalizer: UIO[Unit] =
    ZIO.succeed(println("Finalizing!"))

  val finalized: IO[String, Unit] =
    ZIO.fail("Failed!").ensuring(finalizer)

  val suspendedEffect: RIO[Any, ZIO[Any, IOException, Unit]] =
    ZIO.suspend(ZIO.attempt(Console.printLine("Suspended Hello World!")))
}