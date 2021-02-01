package counter

import dotty.tools.dotc.plugins._

class Plugin extends StandardPlugin {
  val name: String = "counter"
  override val description: String = "Count method calls"

  def init(options: List[String]): List[PluginPhase] =
    val setting = new Setting(if options.isEmpty then None else Some(options(0)))
    (new InstrumentStart(setting)) :: (new InstrumentFinish(setting)) :: Nil
}
