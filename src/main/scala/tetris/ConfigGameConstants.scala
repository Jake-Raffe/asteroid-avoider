package tetris

import common.ConfigGameConstants.objectWidth
import common.{GamePaused, ObjectLocation}

object ConfigGameConstants {

  val sceneXBoundary: Double = 550
  val sceneYBoundary: Double = 600

  // Delay in milliseconds
  val scrollSpeed: Long = 400

  val initialPosition: ObjectLocation = ObjectLocation(sceneXBoundary / 2, 0)
  val initialSquare: Shape            = Square(initialPosition)
  val initialState: State             = State(initialSquare, ExistingBlocks(LowerBoundary.squares), GamePaused)

  val frameScrollMap: Map[Int, Long] =
    Map(
      5   -> 100,
      10  -> 200,
      15  -> 250,
      20  -> 300,
      25  -> 350,
      30  -> 375,
      40  -> 400,
      50  -> 425,
      60  -> 450,
      80  -> 460,
      100 -> 470,
      120 -> 480,
      140 -> 490,
      160 -> 500,
      200 -> 510,
      250 -> 525,
      300 -> 550)
}
