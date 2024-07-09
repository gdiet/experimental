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
  require(weight > 0, s"Weight must be positive but is $weight")
  val AntState(location, power) = board.ants.getOrElse(ant, throw new IllegalArgumentException(s"Unknown ant: $ant"))
  val startElevation =
    board.terrain.fields(location).elevation
      + board.antsAt(location).count(_.player == ant.player) - 1
  val (_, requiredPower) = direction.zipWithIndex.foldLeft(location -> Seq.empty[(XY, Int)]) {
    case (xy -> locations, direction -> index) =>
      val newLocation = relativePosition(xy, board.terrain.size, direction)
      val incline = board.terrain.fields(location).elevation + index + 1 - startElevation
      newLocation -> (locations :+ (newLocation -> math.max(incline, 0) * weight))
  }
  requiredPower
    .takeWhile((_, needed) => needed <= power).lastOption
    .map((newLocation, requiredPower) => AntState(newLocation, power - requiredPower))
