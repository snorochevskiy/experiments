package demo.usemacro

import demo.defmacro._

object MacrosTestApp extends App {
  HelloMacroRaw.hello

  HelloMacroQuasiquotes.hello

  IsEvenMacro.isEvent(3)

  println("List length is " + GenericsMacro.length(List(1,2,3,4,5)))

  LoggerMacro.log("Some text")
}
