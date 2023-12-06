package me.snorochevskiy.myshop.repo

import cats.effect.Async
import cats.syntax.all.*
import doobie.Meta
import doobie.util.transactor.Transactor
import doobie.*
import doobie.implicits.*
import me.snorochevskiy.myshop.model.{EntityNonFound, ProductNotFoundError, *}

import scala.util.Left

trait CatalogRepo[F[_]]:
  def selectProductById(id: Long): F[Either[EntityNonFound, Product]]
  def selectProductInCategory(categoryId: Long): fs2.Stream[F, Product]
  def selectCategoryById(id: Long): F[Either[EntityNonFound, Category]]


class CatalogRepoImpl[F[_] : Async](transactor: Transactor[F]) extends CatalogRepo[F] {

  def selectProductById(id: Long): F[Either[EntityNonFound, Product]] =
    sql"""
      SELECT id, title, description, category_id as categoryId
      FROM products
      WHERE id = $id
    """
      .query[Product].option
      .transact(transactor)
      .map {
        case Some(product) => Right(product)
        case None => Left(ProductNotFoundError)
      }

  def selectProductInCategory(categoryId: Long): fs2.Stream[F, Product] =
    sql"""
      SELECT id, title, description, category_id as categoryId
      FROM products
      WHERE category_id = $categoryId
    """
      .query[Product].stream
      .transact(transactor)

  def selectCategoryById(id: Long): F[Either[EntityNonFound, Category]] =
    sql"""
      SELECT id, title, description, parent_id as parentId
      FROM categories
      WHERE id = $id
    """
      .query[Category].option
      .transact(transactor)
      .map {
        case Some(category) => Right(category)
        case None => Left(CategoryNotFoundError)
      }
}
