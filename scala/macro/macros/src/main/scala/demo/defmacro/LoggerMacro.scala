package demo.defmacro

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

object LoggerMacro {
    def impl(c: Context)(text: c.Expr[String]) : c.Expr[Unit] = {
        import c.universe._
        c.Expr[Unit](q"""println(${c.enclosingPosition.line} + ": " + $text)""")
    }

    def log(text: String): Unit = macro impl
}
