package boxJumper

import common.{GameAtStart, GameConstants, GamePaused, ObjectLocation}

trait BoxJumperConfig extends GameConstants {

  val sceneXBoundary: Double = 600
  val sceneYBoundary: Double = 300
  val stageXBoundary: Double = sceneXBoundary + 2 * objectWidth
  val stageYBoundary: Double = sceneYBoundary + 4 * objectWidth

  val runwayYPosition: Double = sceneYBoundary - objectWidth
  val playerXPosition: Double = 3 * objectWidth

  val runSpeed: Long = 10

  val scrollDistance: Long = 2

  val initialState: State = State(0, List(stageXBoundary), GameAtStart)

  val frameScrollMap: Map[Int, Long] = Map(1000 -> 1, 2000 -> 2, 3000 -> 3)

  lazy val jumpMap: Map[Int, Double] = (0 to 60).map {
    case i if i >= 26 && i <= 34 => i -> (runwayYPosition - 65)
    case i if i >= 21 && i <= 39 => i -> (runwayYPosition - 60)
    case i if i >= 16 && i <= 44 => i -> (runwayYPosition - 50)
    case i if i >= 11 && i <= 49 => i -> (runwayYPosition - 40)
    case i if i >= 6 && i <= 54  => i -> (runwayYPosition - 30)
    case i if i >= 1 && i <= 59  => i -> (runwayYPosition - 15)
    case i                       => i -> runwayYPosition
  }.toMap
}
