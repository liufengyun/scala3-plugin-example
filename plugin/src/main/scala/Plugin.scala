package scala.instrumentation

import dotty.tools.dotc.plugins._

class Plugin extends StandardPlugin {
  val name: String = "scala-instrumentation"
  override val description: String = "Scala instrumentation"

  def init(options: List[String]): List[PluginPhase] = Nil
}
