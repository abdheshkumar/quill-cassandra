package demo

object MainUsage {
  def main(args: Array[String]) {
    val s = Demo.desugar(List(6, 4, 5).sorted)
    println(s)
  }
}
