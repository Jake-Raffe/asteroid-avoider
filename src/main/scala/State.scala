import ConfigConstants.*
import State.{checkShipCollision, square}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Red, White}
import scalafx.scene.shape.Rectangle

import scala.util.Random

case class State(shipPosition: ObjectLocation, asteroids: List[ObjectLocation]) {

  def generateAllObjects: List[Rectangle] = asteroids.map(square(_, White)).appended(square(shipPosition, Red))

  def moveShip(direction: MovementDirection): State = {
    val attemptedPosition: Double = shipPosition.xAxis + direction.xAxisMovement
    val shipOutOfBounds: Boolean  = attemptedPosition < 0 || attemptedPosition > sceneXBoundary
    val shipCrashed: Boolean      = checkShipCollision(ObjectLocation(attemptedPosition, sceneYBoundary), asteroids)
    // TODO collision isn't working (objects are technically before sceneYBoundary?)

    val newShipPosition: ObjectLocation =
      if (shipOutOfBounds) shipPosition else if (shipCrashed) shipStartPosition else shipPosition.copy(xAxis = attemptedPosition)
    State(newShipPosition, asteroids)
  }

  def verticalScroll(): State = {
    val newAsteroid                 = createNewAsteroid(asteroids.headOption.getOrElse(shipStartPosition))
    val scrolledAsteroids           = asteroids.flatMap(moveAsteroid).appended(newAsteroid)
    val resetIfShipCrashed: Boolean = checkShipCollision(shipPosition, asteroids)

    if (resetIfShipCrashed) initialState else State(shipPosition, scrolledAsteroids)
  }

  // Scrolls single asteroid one step down, if past boundary then asteroid is removed
  private def moveAsteroid(asteroid: ObjectLocation): Option[ObjectLocation] = {
    val newY = asteroid.yAxis + (2 * objectWidth)
    if (newY > sceneYBoundary) None else Some(asteroid.copy(yAxis = newY))
  }

  // Creates a random x-coordinate for new asteroid that cannot be the same as previously generated asteroid
  private def createNewAsteroid(previousAsteroid: ObjectLocation): ObjectLocation = {
    val random   = new Random()
    var newXAxis = previousAsteroid.xAxis
    while (newXAxis == previousAsteroid.xAxis)
      newXAxis = random.nextDouble() * sceneXBoundary
    ObjectLocation(newXAxis, 0)
  }
}

object State {

  // Each object (ship and asteroids) is created with this method
  def square(objectLocation: ObjectLocation, colour: Color): Rectangle = new Rectangle {
    x = objectLocation.xAxis
    y = objectLocation.yAxis
    width = objectWidth
    height = objectWidth
    fill = colour
  }

  def checkShipCollision(shipLocation: ObjectLocation, asteroids: List[ObjectLocation]): Boolean =
    asteroids.forall(_ == shipLocation) // TODO add hitbox around ship and asteroids, not just x-coordinate
}
