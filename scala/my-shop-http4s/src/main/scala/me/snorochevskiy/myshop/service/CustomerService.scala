package me.snorochevskiy.myshop.service

import me.snorochevskiy.myshop.util.HashUtil

import cats.effect.kernel.Async
import cats.syntax.all.*
import me.snorochevskiy.myshop.model.Customer
import me.snorochevskiy.myshop.repo.CustomerRepo

import java.security.MessageDigest

class CustomerService[F[_]: Async](repo: CustomerRepo[F]):

  /**
    * Intentionally don't explain the reason why the user hasn't been authenticated
    */
  def loginCustomer(login: String, password: String): F[Option[Customer]] =
    for {
      maybeCustomer <- repo.findCustomerById(login)
      customer = maybeCustomer.filter(c => verifyPassword(password, c.passwd, c.passwdType))
    } yield customer

def verifyPassword(password: String, hash: Array[Byte], encoding: String): Boolean =
  encoding.toUpperCase match {
    case "MD5" =>
      val md5Hash = HashUtil.md5(password)
      java.util.Arrays.equals(hash, md5Hash)
    case _ =>
      false
  }