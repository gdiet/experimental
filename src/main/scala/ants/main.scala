package ants

@main
def main(): Unit =
  val terrain = Terrain(XY(10, 10))
  val ant1 = Ant("1", Player("1"))
  val ant2 = Ant("2", Player("1"))
  val board = Board(terrain, Map(ant1 -> AntState(XY(6, 0), 3), ant2 -> AntState(XY(6, 0), 3)))
  println(maybeMove(ant1, "NENE", 1, board))
