package states

import _root_.states.BoxJumperState.{displayLowerBoundary, square}
import common.*
import scalafx.beans.property.BooleanProperty
import scalafx.geometry.Pos
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Grey, Red, Silver}
import scalafx.scene.shape.{Line, Rectangle}
import scalafx.scene.text.{Font, Text}

import scala.annotation.tailrec
import scala.util.Random
import configs.BoxJumperConfig.*

case class BoxJumperState(jumpFrame: Int, obstacleXPositions: List[Double], gameState: GameState) extends State {
  override val currentGameState: GameState = this.gameState

  private val playerHeight = jumpMap(jumpFrame)

  def startGame(): BoxJumperState = BoxJumperState(jumpFrame, obstacleXPositions, GameInProgress)
  def pauseGame(): BoxJumperState = BoxJumperState(jumpFrame, obstacleXPositions, GamePaused)

  def generateAllObjects: List[Rectangle] = displayLowerBoundary ++ displayObstacles.appended(displayPlayer)

  private val displayPlayer: Rectangle = square(ObjectLocation(playerXPosition, playerHeight), Red)
  private val displayObstacles: List[Rectangle] =
    obstacleXPositions.map(x => square(ObjectLocation(x, runwayYPosition), Silver))

  def jump(): BoxJumperState = BoxJumperState(1, obstacleXPositions, gameState)

  private val incrementJumpFrame: Int = jumpFrame match {
    case 0 | 60  => 0
    case jumping => jumping + 1
  }

  def horizontalScroll(): BoxJumperState = {
    val scrolledObstacles = obstacleXPositions.filter(_ > 0).map(_ - scrollDistance)
    val allObstacles =
      if (scrolledObstacles.length > 3 || scrolledObstacles.exists(_ > (3 * stageXBoundary / 4))) scrolledObstacles
      else scrolledObstacles.appended(stageXBoundary)
    if (objectCollision()) BoxJumperState(jumpFrame, allObstacles, Collision) else BoxJumperState(incrementJumpFrame, allObstacles, gameState)
  }

  private def objectCollision(): Boolean = {
    val collisionLeftBoundary     = playerXPosition - objectWidth
    val collisionRightBoundary    = playerXPosition + objectWidth
    val potentialCollisionObjects = obstacleXPositions.filter(x => x > collisionLeftBoundary && x < collisionRightBoundary)
    val playerAtCollisionHeight   = playerHeight >= runwayYPosition - 25
    playerAtCollisionHeight && potentialCollisionObjects.nonEmpty
  }

  case class HitBox(leftBoundary: Double, rightBoundary: Double)

  def displayText(score: Int): VBox = {
    val text = createTextWithScore(gameState, score / 100)
    new VBox {
      layoutX = objectWidth + (sceneXBoundary - text.boundsInLocal().getWidth) / 2
      layoutY = sceneYBoundary / 3
      alignment = Pos.Center
      children = text
      visible = gameState != GameInProgress
    }
  }
}

object BoxJumperState {

  def square(objectLocation: ObjectLocation, colour: Color): Rectangle = new Rectangle {
    x = objectLocation.xAxis
    y = objectLocation.yAxis
    width = objectWidth
    height = objectWidth
    fill = colour
  }

  private def createLowerBoundary(): List[ObjectLocation] = {
    @tailrec
    def addNextSquare(existingSquares: List[ObjectLocation], boundary: Double): List[ObjectLocation] =
      if (boundary == stageXBoundary) existingSquares
      else
        addNextSquare(existingSquares.appended(ObjectLocation(boundary, sceneYBoundary)), boundary + objectWidth)

    addNextSquare(List(ObjectLocation(0, sceneYBoundary)), 0 + objectWidth)
  }

  val displayLowerBoundary: List[Rectangle] = createLowerBoundary().map(square(_, Grey))
}
