package ants

def relativePosition(location: XY, terrainSize: XY, direction: Char): XY =
  import location.{x, y}, terrainSize.{x => width, y => height}
  XY.apply.tupled(direction match {
    case 'N' => x -> mod(y - 1, height)
    case 'S' => x -> mod(y + 1, height)
    case 'E' => mod(x + 1, width) -> y
    case 'W' => mod(x - 1, width) -> y
    case other =>
      info(s"Invalid direction: $other")
      x -> y
  })

def maybeMove(ant: Ant, direction: String, weight: Int, board: Board): Option[AntState] =
  require(weight > 0)
  val AntState(location, power) = board.ants(ant)
  val startElevation =
    board.terrain.fields(location).elevation
      + board.antsAt(location).count(_.player == ant.player) - 1
  val (_, virtualElevations) = direction.zipWithIndex.foldLeft(location -> Seq.empty[(XY, Int)]) {
    case (xy -> locations, direction -> index) =>
      val newLocation = relativePosition(xy, board.terrain.size, direction)
      val virtualElevation = board.terrain.fields(location).elevation + index + 1
      newLocation -> (locations :+ (newLocation -> math.max(virtualElevation, startElevation)))
  }
  val actualPath =
    virtualElevations.takeWhile((xy, virtualElevation) => (virtualElevation - startElevation) * weight <= power)
  actualPath match // Why Seq and not Vector? See https://stackoverflow.com/q/78722680/1312349
    case Seq() => None
    case _ :+ (xy, virtualElevation) => Some(AntState(xy, power - (virtualElevation - startElevation) * weight))
