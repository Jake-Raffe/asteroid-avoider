package tetris

import common.ConfigGameConstants.objectWidth
import common.ObjectLocation

object ConfigGameConstants {

  val sceneXBoundary: Double = 500
  val sceneYBoundary: Double = 800

  // Delay in milliseconds
  val scrollSpeed: Long = 500

  private val initialSquare: Shape = Square(ObjectLocation(sceneXBoundary/2, 0 + objectWidth))
  val initialState: State          = State(initialSquare, ExistingBlocks(List.empty[ObjectLocation]), gameInMotion = false, collision = false)

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
