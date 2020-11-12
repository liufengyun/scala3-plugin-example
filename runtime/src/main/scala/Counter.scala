package scala.instrumentation

object Counter extends Tracer {
  private var accumulated: Array[Long] = null
  private var count: Array[Long] = null
  private var recursionDepth: Array[Long] = null
  private var callStack = new Array[Int](4096)
  private var subCalls = new Array[Long](4096)
  private var stackPointer: Int = -1

  def init(num: Int) = {
    accumulated = new Array(num)
    count = new Array(num)
    recursionDepth = new Array(num)
  }

  def enter(id: Int): Int =
    count(id) += 1
    recursionDepth(id) += 1
    stackPointer += 1
    callStack(stackPointer) = id
    subCalls(stackPointer) = 1
    stackPointer

  def truncate(frameId: Int) =
    while (stackPointer > frameId) exit()

  def exit() =
    val id = callStack(stackPointer)
    recursionDepth(id) -= 1
    val subCallNum = subCalls(stackPointer)

    // don't duplicate count for recursive calls
    if recursionDepth(id) == 0 then
      accumulated(id) += subCallNum

    stackPointer = stackPointer - 1

    // no need to propagate further
    if stackPointer >= 0 then
      subCalls(stackPointer) += subCallNum

  def finish(outputFile: String) = {
    val file = new java.io.File(outputFile)
    val bw = new java.io.BufferedWriter(new java.io.FileWriter(file))
    (0 until count.size).foreach { id =>
      bw.write(id.toString + ", " + count(id) + ", " + accumulated(id) + "\n")
    }
    bw.close()
  }
}
