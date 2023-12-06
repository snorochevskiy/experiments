package me.snorochevskiy.myshop.repo

import cats.effect.Async
import cats.syntax.all.*
import doobie.Meta
import doobie.util.transactor.Transactor
import doobie.*
import doobie.implicits.*
import me.snorochevskiy.myshop.model.{Customer, EntityNonFound}

trait CustomerRepo[F[_]]:
  def findCustomerById(login: String): F[Option[Customer]]

class CustomerRepoImpl[F[_] : Async](transactor: Transactor[F]) extends CustomerRepo[F]:

  override def findCustomerById(login: String): F[Option[Customer]] =
    sql"""
      SELECT id, login, passwd, passwd_type, title
      FROM customers
      WHERE login = $login
    """
      .query[Customer].option
      .transact(transactor)

