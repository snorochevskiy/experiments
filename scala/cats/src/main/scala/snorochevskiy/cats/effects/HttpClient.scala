package snorochevskiy.cats.effects

import java.io.{DataOutputStream, InputStreamReader, Reader, Writer}
import java.net.{HttpURLConnection, URL}
import java.util.Base64

import cats.effect.{IO, Resource}
import cats.implicits.catsSyntaxFlatMapOps

trait Auth
case class BasicAuth(login: String, password: String)
case class OAuth2(loginUrl: String, authToke: String, password: String, scope: String)

object HttpClient {

  def get(url: String, headers: List[(String,String)] = Nil, auth: Option[Auth] = None): IO[String] = auth match {
    case None =>
      connection(url, "GET", Nil)
        .use(readText)
    case Some(BasicAuth(login, password)) =>
      val secret = Base64.getEncoder.encode(s"$login:$password".getBytes)
      connection(url, "GET", List("Authorization" -> s"Basic $secret"))
        .use(readText)
  }


  def post(url: String, body: String, headers: List[(String,String)] = Nil, auth: Option[Auth] = None): IO[String] =
    connection(url, "POST", Nil) use { conn =>
      for {
        _ <- writeText(conn, body)
        result <- readText(conn)
      } yield result
    }

  private def connection(url: String, method: String, headers: List[(String,String)]): Resource[IO, HttpURLConnection] =
    Resource.make {
      IO {
        val conn = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
        conn.setRequestMethod(method)
        headers.foreach { case (name, value) =>
          conn.setRequestProperty(name, value)
        }
        conn
      }
    } { conn =>
      IO(conn.disconnect()).handleErrorWith(_ => IO.unit)
    }

  private def readText(conn: HttpURLConnection): IO[String] =
    openReader(conn).use(readFromReader)

  private def openReader(conn: HttpURLConnection): Resource[IO, Reader] =
    Resource.make {
      IO(new InputStreamReader(conn.getInputStream))
    } {r => IO(r.close()).handleErrorWith(_ => IO.unit)}

  private def readFromReader(in: Reader): IO[String] =
    for {
      buffer <- IO(new Array[Char](1024 * 10))
      accum  <- IO(new StringBuilder)
      result <- transmit(in, accum, buffer, 0L)
    } yield result._1.toString

  private def transmit(origin: Reader, sb: StringBuilder, buffer: Array[Char], acc: Long): IO[(StringBuilder,Long)] =
    for {
      amount <- IO {origin.read(buffer, 0, buffer.length)}
      count  <- if (amount > -1) IO(sb.appendAll(buffer, 0, amount)) >> transmit(origin, sb, buffer, acc + amount)
      else IO.pure(sb -> acc)
    } yield count

  private def writeText(conn: HttpURLConnection, payload: String): IO[Unit] =
    openOutputStream(conn).use { os =>
      IO {
        os.writeBytes(payload)
        os.flush()
      }
    }

  private def openOutputStream(conn: HttpURLConnection): Resource[IO, DataOutputStream] =
    Resource.make {
      IO {
        conn.setDoOutput(true)
        new DataOutputStream(conn.getOutputStream)
      }
    } { os => IO{os.close()}.handleErrorWith(_ => IO.unit) }

}

object OAuthAuthenticator {

  def authenticate(oAuth2: OAuth2): IO[String] = {
    val headers: List[(String,String)] = List("Content-Type" -> "application/x-www-form-urlencoded")
    val payload = s"client_id=${oAuth2.authToke}&client_secret=${oAuth2.password}&scope=${oAuth2.scope}&grant_type=client_credentials"
    for {
      resp <- HttpClient.post(oAuth2.loginUrl, payload, headers)
    } yield resp
  }
}

case class WebSecResponse(
                           `access_token`: String,
                           `token_type`: String,
                           `expires_in`: Long // Integer. The number of seconds the access token is valid.
                         )