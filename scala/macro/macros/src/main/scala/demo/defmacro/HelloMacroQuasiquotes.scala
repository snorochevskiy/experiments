package demo.defmacro

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

object HelloMacroQuasiquotes {
  def impl(c: Context) : c.Expr[Unit] = {
    import c.universe._
    val result = q"""println("Hello macro!")"""
    println(showRaw(result))
    c.Expr[Unit](result)
  }

  def hello: Unit = macro impl
}
