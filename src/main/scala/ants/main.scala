package ants

import scala.util.chaining.scalaUtilChainingOps

@main
def main(): Unit =
  val terrain = Terrain(XY(35, 15), Map(XY(6, 3) -> Cell(3, 0)))
  val ant1 = Ant("1", Player("1"))
  val ant2 = Ant("2", Player("1"))
  val board = Board(terrain, Map(ant1 -> AntState(XY(6, 0), 3), ant2 -> AntState(XY(16, 9), 3)))
  //  println(maybeMove(ant1, "NENE", 1, board))
  //  visibleFields(board).foreach((player, locations) => {
  //    println(s"\n$player")
  //
  //    val boardChars = Array.fill(board.terrain.size.y, board.terrain.size.x)('.')
  //    locations.foreach(xy => boardChars(xy.y)(xy.x) = 'X')
  //    boardChars.foreach(row => println(row.mkString))
  //
  //    println()
  //  })
  phaseFood(board).tap { b =>
    val boardChars = Array.fill(board.terrain.size.y, board.terrain.size.x)('.')
    println(b.terrain.fields)
    b.terrain.fields.foreach { case (xy, field) =>
      if (field.food > 0)
        boardChars(xy.y)(xy.x) = 'F'
    }
    boardChars.foreach(row => println(row.mkString))
  }
