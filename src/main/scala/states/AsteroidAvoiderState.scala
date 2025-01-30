package states

import common.*
import configs.AsteroidAvoiderConfig.*
import scalafx.beans.property.BooleanProperty
import scalafx.geometry.Pos
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Black, Red, White}
import scalafx.scene.shape.Rectangle
import states.AsteroidAvoiderState.{shipHasCrashed, square}

import scala.util.Random

case class AsteroidAvoiderState(shipPosition: ObjectLocation, asteroids: List[ObjectLocation], gameState: GameState) extends State {
  override val currentGameState: GameState = this.gameState

  private val asteroidColour: Color = if (gameState == Collision) Red else White
  private val shipColour: Color     = if (gameState == Collision) White else Red

  def startGame(): AsteroidAvoiderState = AsteroidAvoiderState(shipPosition, asteroids, GameInProgress)
  def pauseGame(): AsteroidAvoiderState = AsteroidAvoiderState(shipPosition, asteroids, GamePaused)

  def generateAllObjects: List[Rectangle] = asteroids.map(square(_, asteroidColour)).appended(square(shipPosition, shipColour))

  def moveShip(direction: MovementDirection): AsteroidAvoiderState = // TODO add vertical ship movement?
    if (gameState == GameInProgress) {
      val attemptedPosition: Double = shipPosition.xAxis + direction.xAxisMovement
      val shipOutOfBounds: Boolean  = attemptedPosition < 0 || attemptedPosition > sceneXBoundary

      if (shipHasCrashed(ObjectLocation(attemptedPosition, shipYPosition), asteroids)) {
        val crashedPosition = shipPosition.copy(xAxis = shipPosition.xAxis + (direction.xAxisMovement / 2))
        AsteroidAvoiderState(crashedPosition, asteroids, Collision)
      } else {
        val newShipPosition: ObjectLocation = if (shipOutOfBounds) shipPosition else shipPosition.copy(xAxis = attemptedPosition)
        AsteroidAvoiderState(newShipPosition, asteroids, gameState)
      }
    } else AsteroidAvoiderState(shipPosition, asteroids, gameState)

  def verticalScroll(): AsteroidAvoiderState = {
    val firstNewAsteroid  = createNewAsteroid(asteroids.headOption.getOrElse(shipStartPosition))
    val secondNewAsteroid = createNewAsteroid(firstNewAsteroid)
    val scrolledAsteroids = asteroids.flatMap(moveAsteroid) ++ List(firstNewAsteroid, secondNewAsteroid)
    if (shipHasCrashed(shipPosition, scrolledAsteroids))
      AsteroidAvoiderState(shipPosition, asteroids.flatMap(moveAsteroidHalfPosition), Collision)
    else AsteroidAvoiderState(shipPosition, scrolledAsteroids, gameState)
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

  def displayText(score: Int): VBox = {
    val text = createTextWithScore(gameState, score / 5)
    new VBox {
      layoutX = (stageXBoundary - text.boundsInLocal().getWidth) / 2
      layoutY = sceneYBoundary / 3
      alignment = Pos.Center
      children = text
      visible = gameState != GameInProgress
    }
  }
}

object AsteroidAvoiderState {

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
