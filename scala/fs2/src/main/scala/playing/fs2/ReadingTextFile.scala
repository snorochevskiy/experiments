package playing.fs2

import cats.effect.IO
import fs2.Stream
import fs2.io.file.{Files, Path}

object ReadingTextFile {

  import cats.effect.unsafe.implicits.global

  def main(args: Array[String]): Unit = {
    val filesRead: Stream[IO, Byte] = Files[IO].readAll(Path("/etc/fstab"))
    val textStream: Stream[IO, String] = filesRead.through(fs2.text.utf8.decode)
    println(textStream.compile.string.unsafeRunSync())
  }

}
