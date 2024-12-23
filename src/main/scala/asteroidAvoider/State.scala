package asteroidAvoider

import asteroidAvoider.ConfigGameConstants.*
import asteroidAvoider.State.{shipHasCrashed, square}
import common.ConfigGameConstants.objectWidth
import common.{HitBox, MovementDirection, ObjectLocation}
import scalafx.beans.property.BooleanProperty
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Black, Red, White}
import scalafx.scene.shape.Rectangle

import scala.util.Random

case class State(shipPosition: ObjectLocation, asteroids: List[ObjectLocation], gameInMotion: Boolean, collision: Boolean) {
  private val asteroidColour: Color = if (collision) Red else White
  private val shipColour: Color     = if (collision) White else Red

  def startGame(): State = State(shipPosition, asteroids, gameInMotion = true, collision)
  def pauseGame(): State = State(shipPosition, asteroids, gameInMotion = false, collision)

  def generateAllObjects: List[Rectangle] = asteroids.map(square(_, asteroidColour)).appended(square(shipPosition, shipColour))

  def moveShip(direction: MovementDirection): State = // TODO add vertical ship movement?
    if (gameInMotion) {
      val attemptedPosition: Double = shipPosition.xAxis + direction.xAxisMovement
      val shipOutOfBounds: Boolean  = attemptedPosition < 0 || attemptedPosition > sceneXBoundary

      if (shipHasCrashed(ObjectLocation(attemptedPosition, shipYPosition), asteroids)) {
        State(shipPosition, asteroids, gameInMotion = false, collision = true)
      } else {
        val newShipPosition: ObjectLocation = if (shipOutOfBounds) shipPosition else shipPosition.copy(xAxis = attemptedPosition)
        State(newShipPosition, asteroids, gameInMotion, collision)
      }
    } else State(shipPosition, asteroids, gameInMotion, collision)

  def verticalScroll(): State = {
    val newAsteroid          = createNewAsteroid(asteroids.headOption.getOrElse(shipStartPosition))
    val scrolledAsteroids    = asteroids.flatMap(moveAsteroid).appended(newAsteroid)
    val shipCrashed: Boolean = shipHasCrashed(shipPosition, scrolledAsteroids)

    State(shipPosition, scrolledAsteroids, gameInMotion = !shipCrashed, collision = shipCrashed)
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

  def shipHasCrashed(shipLocation: ObjectLocation, asteroids: List[ObjectLocation]): Boolean = {
    val maybeAsteroid: Option[ObjectLocation] = asteroids.find(_.yAxis == shipYPosition)
    maybeAsteroid.fold(false) { asteroid =>
      val asteroidHB  = HitBox(asteroid.xAxis, asteroid.xAxis + objectWidth)
      val shipHB      = HitBox(shipLocation.xAxis, shipLocation.xAxis + objectWidth)
      val noCollision = asteroidHB.rightBoundary < shipHB.leftBoundary || asteroidHB.leftBoundary > shipHB.rightBoundary
      if (!noCollision) println("*** SHIP CRASHED ***")
      !noCollision
    }
  }
}
