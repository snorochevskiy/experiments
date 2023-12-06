package e2_di

import zio.*

object DiMain extends ZIOAppDefault:
  override val run =
    val program = for {
      printer <- ZIO.service[PrinterService]
      _        = printer.print()
    } yield ()
    program
      .provide(GreeterService.layer, PrinterService.layer)


class PrinterService(greeter: GreeterService):
  def print() = println(greeter.greet())

object PrinterService:
  val layer: ZLayer[GreeterService, Nothing, PrinterService] = ZLayer {
    for {
      greeter <- ZIO.service[GreeterService]
    } yield new PrinterService(greeter)
  }

class GreeterService:
  def greet(): String = "Hello"

object GreeterService:
  val layer: ZLayer[Any, Nothing, GreeterService] =
    ZLayer.succeed {
      new GreeterService()
    }

