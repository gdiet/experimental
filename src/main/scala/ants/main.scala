package ants

@main
def main(): Unit =
  val terrain = Terrain(XY(20, 10), Map(XY(6, 3) -> Cell(3, 0)))
  val ant1 = Ant("1", Player("1"))
  val ant2 = Ant("2", Player("1"))
  val board = Board(terrain, Map(ant1 -> AntState(XY(6, 0), 3), ant2 -> AntState(XY(16, 9), 3)))
  println(maybeMove(ant1, "NENE", 1, board))
  visibility(board).foreach((player, locations) => {
    println(s"\n$player")

    val boardChars = Array.fill(board.terrain.size.y, board.terrain.size.x)('.')
    locations.foreach(xy => boardChars(xy.y)(xy.x) = 'X')
    boardChars.foreach(row => println(row.mkString))

    println()
  })
