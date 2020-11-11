package scala.instrumentation

object Runtime {
  private[this] var accumulated: Array[Long] = null
  private[this] var count: Array[Long] = null
  private[this] var recursionDepth: Array[Long] = null
  private[this] var callStack = new Array[Int](4096)
  private[this] var subCalls = new Array[Long](4096)
  private[this] var stackPointer: Int = -1

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

  def dump(path: String) = {
    val file = new java.io.File(path)
    val bw = new java.io.BufferedWriter(new java.io.FileWriter(file))
    (0 until count.size).foreach { id =>
      bw.write(id.toString + ", " + count(id) + ", " + accumulated(id) + "\n")
    }
    bw.close()
  }
}
