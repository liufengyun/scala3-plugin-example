object Hello {
  def main(args: Array[String]): Unit = {
    foo(5)
  }

  def foo(x: Int): Unit =
    if (x > 0)
      foo(x - 1)
    else bar()

  def bar(): Unit = println("hello")
}
