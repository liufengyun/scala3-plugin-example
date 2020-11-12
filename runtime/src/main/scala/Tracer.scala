package scala.instrumentation

trait Tracer {
  /** Initialization */
  def init(num: Int): Unit

  /** Enter a method
   *
   *  return frame ID, which will be used for exception handling
   */
  def enter(id: Int): Int

  /** Truncate the stack frame
   *
   *  It is called when an exception is caught at the specified stack frame
   */
  def truncate(frameId: Int): Unit

  /** Method exit */
  def exit(): Unit

  /** Program finish */
  def finish(outputFile: String): Unit
}
