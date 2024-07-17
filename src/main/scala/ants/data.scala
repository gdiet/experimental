package ants

case class XY(x: Int, y: Int)

case class Player(id: String)

case class Ant(id: String, player: Player)

case class AntState(xy: XY, power: Int)

sealed trait Field:
  val elevation: Int
  val food: Int
  def addFood(count: Int): Field

case class Nest(elevation: Int, food: Int) extends Field:
  override def addFood(count: Int): Nest = copy(food = food + count)

case class Cell(elevation: Int, food: Int) extends Field:
  override def addFood(count: Int): Cell = copy(food = food + count)

case class Terrain(size: XY, fields: Map[XY, Field])
object Terrain:
  def apply(size: XY, fields: Map[XY, Field] = Map().withDefaultValue(Cell(0, 0))): Terrain =
    new Terrain(size, fields.withDefaultValue(Cell(0, 0)))

case class Board(terrain: Terrain, ants: Map[Ant, AntState]) {
  lazy val antsAt: Map[XY, Set[Ant]] = ants.groupBy(_._2.xy).map((xy, entries) => xy -> entries.keySet)
  def _terrain(f: Terrain => Terrain): Board = copy(terrain = f(terrain))
  def _fields(f: Map[XY, Field] => Map[XY, Field]): Board = _terrain(_.copy(fields = f(terrain.fields)))
}
