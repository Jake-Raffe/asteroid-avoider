package configs

import common.{GameAtStart, GamePaused, ObjectLocation}
import states.*

case object TetrisConfig extends GameConstants {

  val gameTitle: String = "Tetris"

  val maxShapeTypes: Int = 8

  val newShapeMap: Map[Int, (ObjectLocation, Orientation) => TetrisShape] =
    Map(
      1 -> Square.apply,
      2 -> Line.apply,
      3 -> RectangleShape.apply,
      4 -> LShape.apply,
      5 -> LShapeRev.apply,
      6 -> Zigzag.apply,
      7 -> ZigzagRev.apply,
      8 -> TShape.apply)

  val sceneXBoundary: Double = 300
  val sceneYBoundary: Double = 600
  val stageXBoundary: Double = sceneXBoundary + 2 * objectWidth
  val stageYBoundary: Double = sceneYBoundary + 4 * objectWidth

  // Delay in milliseconds
  val scrollSpeed: Long = 300

  val initialPosition: ObjectLocation = ObjectLocation(sceneXBoundary / 2, 0)
  val initialSquare: TetrisShape      = Square(initialPosition, Neutral)
  val initialState: TetrisState       = TetrisState(initialSquare, ExistingBlocks(List.empty), GameAtStart)

  val frameScrollMap: Map[Int, Long] = Map(500 -> 25, 1000 -> 50, 1500 -> 75, 2000 -> 100)
}
