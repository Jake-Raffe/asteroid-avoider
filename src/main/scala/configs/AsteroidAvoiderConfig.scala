package configs

import common.{GameAtStart, ObjectLocation}
import states.AsteroidAvoiderState

case object AsteroidAvoiderConfig extends GameConstants {

  val gameTitle: String = "Asteroid Avoider"

  // Scene: this is the game field that the game actually exists in
  val sceneXBoundary: Double = 600
  val sceneYBoundary: Double = 600
  // Stage/Window: this is the window that we see, W/H can be set and edited
  val stageXBoundary: Double = sceneXBoundary + objectWidth
  val stageYBoundary: Double = sceneYBoundary + 3 * objectWidth

  private val shipStartXPosition: Double = sceneXBoundary / 2
  val shipYPosition: Double              = sceneYBoundary - (2 * objectWidth)
  val shipStartPosition: ObjectLocation  = ObjectLocation(shipStartXPosition, shipYPosition)

  val scrollSpeed: Long = 800

  val initialState: AsteroidAvoiderState = AsteroidAvoiderState(shipStartPosition, List.empty[ObjectLocation], GameAtStart)

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
