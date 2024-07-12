package ants

def relativePosition(location: XY, terrainSize: XY, dx: Int, dy: Int): XY =
  XY(mod(location.x + dx, terrainSize.x), mod(location.y + dy, terrainSize.y))

def relativePosition(location: XY, terrainSize: XY, direction: Char): Option[XY] =
  direction match
    case 'N' => Some(relativePosition(location, terrainSize, 0, -1))
    case 'S' => Some(relativePosition(location, terrainSize, 0, 1))
    case 'W' => Some(relativePosition(location, terrainSize, -1, 0))
    case 'E' => Some(relativePosition(location, terrainSize, 1, 0))
    case other => None

def maybeMove(ant: Ant, direction: String, weight: Int, board: Board): Option[AntState] =
  require(weight > 0, s"Weight must be positive but is $weight")
  val AntState(location, power) = board.ants.getOrElse(ant, throw new IllegalArgumentException(s"Unknown ant: $ant"))
  val startElevation =
    board.terrain.fields(location).elevation
      + board.antsAt(location).count(_.player == ant.player) - 1
  val (_, requiredPower) = direction.zipWithIndex.foldLeft(location -> Seq.empty[(XY, Int)]) {
    case (xy -> locations, direction -> index) =>
      relativePosition(xy, board.terrain.size, direction) match
        case None => xy -> (locations :+ (xy -> Int.MaxValue))
        case Some(newLocation) =>
          val incline = board.terrain.fields(newLocation).elevation + index + 1 - startElevation
          newLocation -> (locations :+ (newLocation -> math.max(incline, 1) * weight))
  }
  requiredPower
    .takeWhile((_, needed) => needed <= power).lastOption
    .map((newLocation, requiredPower) => AntState(newLocation, power - requiredPower))
