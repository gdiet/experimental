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

/** dx, dy, distance for all fields in a +-3 square with distance > 0 and < 5 */
val visibilityOffsets: Set[(Int, Int, Int)] =
  (for
    x <- -3 to 3; y <- -3 to 3
    if x != 0 || y != 0
    dist = math.abs(x) + math.abs(y)
    if dist < 5
  yield (x, y, dist)).toSet

/** visibilityOffsets area around ants where elevation difference + distance - number of ants < 5 */
def visibility(board: Board): Map[Player, Set[XY]] =
  import board.terrain.fields

  val numberOfAntsByPlayerAndLocation: Map[Player, Map[XY, Int]] =
    board.ants
      .groupBy(_._1.player)
      .map((player, ants) => player -> ants.groupBy(_._2.xy).map((xy, ants) => xy -> ants.size))

  numberOfAntsByPlayerAndLocation.map((player, locations) =>
    player -> locations.foldLeft(locations.keySet) { case (visibleLocations, (location, numberOfAnts)) =>
      val elevation = fields(location).elevation
      val candidates =
        visibilityOffsets.map(relativePosition(location, board.terrain.size, _, _) -> _).toMap
          -- visibleLocations
      candidates.collect {
        case (xy, distance)
          if math.abs(fields(xy).elevation - elevation) + distance - numberOfAnts < 5 =>
          xy
      }.toSet
    }
  )
