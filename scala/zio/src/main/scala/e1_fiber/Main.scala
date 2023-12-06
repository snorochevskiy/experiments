package e1_fiber

import zio._


object Main:
  def main(args: Array[String]): Unit =
    val program = ZIO.succeed(5)
      .map(a => a + 1)
      .flatMap(a => ZIO.succeed(a * 2))

    val result = zio.Unsafe.unsafe { implicit unsafe =>
      zio.Runtime.default.unsafe.run(program)
        .getOrThrowFiberFailure()
    }

    print(result)


//  def main(args: Array[String]): Unit =
//    val echoProgram = zio.Console.printLine("Hello ZIO!")
//
//    zio.Unsafe.unsafe {
//      given unsafe
//
//      =>
//      zio.Runtime.default.unsafe.run(program)
//        .getOrThrowFiberFailure()
//    }

