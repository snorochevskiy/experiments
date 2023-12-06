package demo.defmacro

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

object GenericsMacro {
  def impl[T: c.WeakTypeTag](c: Context)(list: c.Expr[List[T]]) : c.Expr[Int] = {
    import c.universe._
    c.Expr[Int](q"""$list.size""")
  }

  def length[T](list: List[T]): Int = macro impl[T]
}
