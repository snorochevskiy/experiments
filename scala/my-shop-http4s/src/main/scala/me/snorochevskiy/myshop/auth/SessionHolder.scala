package me.snorochevskiy.myshop.auth

import cats.effect.Async

import java.util.UUID

case class UserSession(
  id: Long,
  name: String
)

class SessionHolder[F[_]: Async]:
  val sessions: collection.mutable.HashMap[String, UserSession] = collection.mutable.HashMap()

  def create(session: UserSession): F[String] =
    val uuid = UUID.randomUUID().toString
    sessions.put(uuid, session)
    Async[F].pure(uuid)

  def put(token: String, session: UserSession): F[Unit] =
    Async[F].pure(sessions.put(token, session))

  def find(token: String): F[Option[UserSession]] =
    Async[F].pure(sessions.get(token))