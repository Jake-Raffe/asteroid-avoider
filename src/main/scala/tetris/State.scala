package tetris

import asteroidAvoider.ConfigGameConstants.*
import asteroidAvoider.State.{shipHasCrashed, square}
import common.*
import common.ConfigGameConstants.objectWidth
import scalafx.beans.property.BooleanProperty
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Black, Grey, Red, White}
import scalafx.scene.shape.Rectangle

import scala.util.Random

case class State(currentShape: Shape, existingBlocks: ExistingBlocks, gameInMotion: Boolean, collision: Boolean) {

  def startGame(): State = State(currentShape, existingBlocks, gameInMotion = true, collision)
  def pauseGame(): State = State(currentShape, existingBlocks, gameInMotion = false, collision)

  def generateAllObjects: List[Rectangle] = existingBlocks.toDisplayObjects ++ currentShape.toDisplayObjects

  def moveShape(direction: MovementDirection): State =
    if (gameInMotion) {
      val newPosition = direction match {
        case Left if currentShape.leftBoundary == 0                => currentShape
        case Left                                                  => currentShape.moveLeft()
        case Right if currentShape.rightBoundary == sceneXBoundary => currentShape
        case Right                                                 => currentShape.moveRight()
      }
      State(newPosition, existingBlocks, gameInMotion, collision)
    } else State(currentShape, existingBlocks, gameInMotion, collision)

  def shapeDescends(): State = {
    val shapePosition = if (currentShape.lowerBoundary.yAxis != sceneYBoundary) currentShape.moveDown() else currentShape
    State(shapePosition, existingBlocks, gameInMotion, collision)
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
