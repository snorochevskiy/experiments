package demo.defmacro

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

object HelloMacroRaw {
    def hello: Unit = macro impl

    def impl(c: Context): c.Expr[Unit] = {
        import c.universe._
        c.Expr[Unit](
            Apply(
                Ident(TermName("println")),
                List(Literal(Constant("Hello macro!")))
            )
        )
    }
}
