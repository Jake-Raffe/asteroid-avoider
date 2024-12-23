package asteroidAvoider

import asteroidAvoider.ConfigGameConstants.*
import asteroidAvoider.State.{shipHasCrashed, square}
import common.*
import common.ConfigGameConstants.{gameOverText, objectWidth, pausedText, startGameText}
import scalafx.beans.property.BooleanProperty
import scalafx.geometry.Pos
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Black, Red, White}
import scalafx.scene.shape.Rectangle

import scala.util.Random

case class State(shipPosition: ObjectLocation, asteroids: List[ObjectLocation], gameState: GameState) {
  private val asteroidColour: Color = if (gameState == Collision) Red else White
  private val shipColour: Color     = if (gameState == Collision) White else Red

  def startGame(): State = State(shipPosition, asteroids, GameInProgress)
  def pauseGame(): State = State(shipPosition, asteroids, GamePaused)

  def generateAllObjects: List[Rectangle] = asteroids.map(square(_, asteroidColour)).appended(square(shipPosition, shipColour))

  def moveShip(direction: MovementDirection): State = // TODO add vertical ship movement?
    if (gameState == GameInProgress) {
      val attemptedPosition: Double = shipPosition.xAxis + direction.xAxisMovement
      val shipOutOfBounds: Boolean  = attemptedPosition < 0 || attemptedPosition > sceneXBoundary

      if (shipHasCrashed(ObjectLocation(attemptedPosition, shipYPosition), asteroids)) {
        val crashedPosition = shipPosition.copy(xAxis = shipPosition.xAxis + (direction.xAxisMovement / 2))
        State(crashedPosition, asteroids, Collision)
      } else {
        val newShipPosition: ObjectLocation = if (shipOutOfBounds) shipPosition else shipPosition.copy(xAxis = attemptedPosition)
        State(newShipPosition, asteroids, gameState)
      }
    } else State(shipPosition, asteroids, gameState)

  def verticalScroll(): State = {
    val firstNewAsteroid  = createNewAsteroid(asteroids.headOption.getOrElse(shipStartPosition))
    val secondNewAsteroid = createNewAsteroid(firstNewAsteroid)
    val scrolledAsteroids = asteroids.flatMap(moveAsteroid) ++ List(firstNewAsteroid, secondNewAsteroid)
    if (shipHasCrashed(shipPosition, scrolledAsteroids)) State(shipPosition, asteroids.flatMap(moveAsteroidHalfPosition), Collision)
    else State(shipPosition, scrolledAsteroids, gameState)
  }

  // Scrolls single asteroid one step down, if past boundary then asteroid is removed
  private def moveAsteroid(asteroid: ObjectLocation): Option[ObjectLocation] = {
    val newY = asteroid.yAxis + (2 * objectWidth)
    if (newY > sceneYBoundary) None else Some(asteroid.copy(yAxis = newY))
  }
  private def moveAsteroidHalfPosition(asteroid: ObjectLocation): Option[ObjectLocation] = {
    val newY = asteroid.yAxis + objectWidth
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

  def displayText(): VBox = {
    val text = gameState match {
      case GameAtStart => startGameText
      case Collision   => gameOverText
      case _           => pausedText
    }
    new VBox {
      layoutX = (stageXBoundary - text.boundsInLocal().getWidth) / 2
      layoutY = sceneYBoundary / 3
      alignment = Pos.Center
      children = text
      visible = gameState != GameInProgress
    }
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
    val shipHB          = HitBox(shipLocation.xAxis, shipLocation.xAxis + objectWidth)
    val bottomAsteroids = asteroids.filter(_.yAxis == shipYPosition)
    bottomAsteroids.exists { asteroid =>
      val asteroidHB  = HitBox(asteroid.xAxis, asteroid.xAxis + objectWidth)
      val noCollision = asteroidHB.rightBoundary < shipHB.leftBoundary || asteroidHB.leftBoundary > shipHB.rightBoundary
      if (!noCollision) println("*** SHIP CRASHED ***")
      !noCollision
    }
  }
}
