package me.snorochevskiy.myshop.service

import cats.effect.kernel.Async
import cats.syntax.all.*
import me.snorochevskiy.myshop.model.{CategoryNotFoundError, EntityNonFound, Product}
import me.snorochevskiy.myshop.repo.CatalogRepo

class CatalogService[F[_]: Async](repo: CatalogRepo[F]):

  def getProduct(productId: Long): F[Either[EntityNonFound, Product]] =
    repo.selectProductById(productId)

  def buildProductBreadcrumbs(productId: Long): F[Either[EntityNonFound, List[Long]]] =
    for {
      eitherProduct     <- repo.selectProductById(productId)
      eitherBreadCrumbs <- eitherProduct.map(_.categoryId).flatTraverse(buildCategoryPath)
    } yield eitherBreadCrumbs

  private def buildCategoryPath(catalogId: Long): F[Either[EntityNonFound, List[Long]]] =
    def nested(acc: List[Long], catalogId: Long): F[Either[EntityNonFound, List[Long]]] =
      repo.selectCategoryById(catalogId).flatMap(
        _.flatTraverse(
          _.parentId match
            case Some(p) => nested(p :: acc, p)
            case None => acc.asRight[CategoryNotFoundError.type].pure[F]
        )
      )
    nested(List(catalogId), catalogId)
