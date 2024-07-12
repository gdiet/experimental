package ants

import org.scalatest._
import flatspec._
import matchers._

class MaybeMoveSpec extends AnyFlatSpec with should.Matchers:
  val terrain = Terrain(XY(10, 10))
  val ant = Ant("1", Player("A"))
  val ant2 = Ant("2", Player("A"))
  val ant3 = Ant("3", Player("A"))
  val ant1b = Ant("1", Player("B"))

  "Not enough power: 'NN' from (6, 6) with power 1 and weight 2" should "result in None with power 1" in {
    val board = Board(terrain, Map(ant -> AntState(XY(6, 6), 1)))
    val result = maybeMove(ant, "NN", 2, board)
    result should be(None)
  }

  "Simple move: 'NN' from (6, 6) with power 1 and weight 1" should "result in (6, 5) with power 0" in {
    val board = Board(terrain, Map(ant -> AntState(XY(6, 6), 1)))
    val result = maybeMove(ant, "NN", 1, board)
    result should be(Some(AntState(XY(6, 5), 0)))
  }

  "Two steps, weight 2: 'NNN' from (6, 6) with power 5 and weight 2" should "result in (6, 4) with power 1" in {
    val board = Board(terrain, Map(ant -> AntState(XY(6, 6), 5)))
    val result = maybeMove(ant, "NNN", 2, board)
    result should be(Some(AntState(XY(6, 4), 1)))
  }

  "Wrap around at board edges: 'NW' from (0, 0) with power 2 and weight 1" should "result in (9, 9) with power 0" in {
    val board = Board(terrain, Map(ant -> AntState(XY(0, 0), 2)))
    val result = maybeMove(ant, "NW", 1, board)
    result should be(Some(AntState(XY(9, 9), 0)))
  }

  "Invalid direction: 'XN' from (6, 6) with power 2 and weight 1" should "result in None" in {
    val board = Board(terrain, Map(ant -> AntState(XY(6, 6), 2)))
    val result = maybeMove(ant, "XN", 1, board)
    result should be(None)
  }

  "Invalid direction: 'NXN' from (6, 6) with power 2 and weight 1" should "result in (6, 5) with power 1" in {
    val board = Board(terrain, Map(ant -> AntState(XY(6, 6), 2)))
    val result = maybeMove(ant, "NXN", 1, board)
    result should be(Some(AntState(XY(6, 5), 1)))
  }

  "Downhill move: 'NNNN' from (6, 6) elevation 2 with power 1 and weight 1" should "result in (6, 4) with power 0" in {
    val terrain = Terrain(XY(10, 10), Map(XY(6, 6) -> Cell(2, 0)))
    val board = Board(terrain, Map(ant -> AntState(XY(6, 6), 1)))
    val result = maybeMove(ant, "NNNN", 1, board)
    result should be(Some(AntState(XY(6, 3), 0)))
  }

  "Move from ant stack: 'NNNN' from (6, 6) where 3 own and one foreign ants are with power 1 and weight 1" should "result in (6, 4) with power 0" in {
    val terrain = Terrain(XY(10, 10))
    val antState = AntState(XY(6, 6), 1)
    val board = Board(terrain, Map(
      ant -> antState,
      ant2 -> antState,
      ant3 -> antState,
      ant1b -> antState
    ))
    val result = maybeMove(ant, "NNNN", 1, board)
    result should be(Some(AntState(XY(6, 3), 0)))
  }
