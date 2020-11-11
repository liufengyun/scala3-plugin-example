package scala.instrumentation

import dotty.tools.dotc._
import core._
import Contexts.Context
import plugins._

class InstrumentPhase extends PluginPhase {
  val phaseName = "instrumentPhase"
}
