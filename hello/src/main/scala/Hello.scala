object Hello {
  def main(args: Array[String]): Unit = {
    def warmup = (0 to 1000).foreach(foo)
    foo(5)
  }

  def foo(x: Int): Unit =
    if (x > 0)
      foo(x - 1)
    else bar()

  def bar(): Unit = println("hello")
}
