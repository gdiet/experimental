package ants

case class XY(x: Int, y: Int)

case class Player(id: String)

case class Ant(id: String, player: Player)

case class AntState(xy: XY, power: Int)

sealed trait Field:
  val elevation: Int
  val food: Int

case class Nest(elevation: Int, food: Int) extends Field

case class Cell(elevation: Int, food: Int) extends Field

case class Terrain(size: XY, fields: Map[XY, Field] = Map().withDefaultValue(Cell(0, 0)))

case class Board(terrain: Terrain, ants: Map[Ant, AntState]) {
  lazy val antsAt: Map[XY, Set[Ant]] = ants.groupBy(_._2.xy).map((xy, entries) => xy -> entries.keySet)
}
