package tetris

import common.ConfigGameConstants.objectWidth
import common.{GameAtStart, GamePaused, ObjectLocation}

object ConfigGameConstants {

  val sceneXBoundary: Double = 300
  val sceneYBoundary: Double = 600
  val stageXBoundary: Double = sceneXBoundary + 2 * objectWidth
  val stageYBoundary: Double = sceneYBoundary + 4 * objectWidth

  // Delay in milliseconds
  val scrollSpeed: Long = 300

  val initialPosition: ObjectLocation = ObjectLocation(sceneXBoundary / 2, 0)
  val initialSquare: Shape            = Square(initialPosition, Neutral)
  val initialState: State             = State(initialSquare, ExistingBlocks(List.empty), GameAtStart)

  val frameScrollMap: Map[Int, Long] = Map(500 -> 25, 1000 -> 50, 1500 -> 75, 2000 -> 100)
}
