package demo.defmacro

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

object IsEvenMacro {
    def impl(c: Context)(number: c.Expr[Int]): c.Expr[Boolean] = {
        import c.universe._

        c.Expr[Boolean](q"""
            if ($number % 2 == 0){
                println($number.toString + " is even")
                true
            } else {
                println($number.toString + " is odd")
                false
            }
        """)
    }

    def isEvent(number: Int): Boolean = macro impl
}
