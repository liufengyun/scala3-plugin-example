package scala.instrumentation

import dotty.tools.dotc._
import core._
import Contexts.Context
import plugins._

class InstrumentPhase(setting: Setting) extends PluginPhase {
  val phaseName = "instrumentPhase"
}
