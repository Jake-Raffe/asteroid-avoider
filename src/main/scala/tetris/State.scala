package tetris

import common.*
import common.ConfigGameConstants.objectWidth
import scalafx.beans.property.BooleanProperty
import scalafx.geometry.Pos
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Black, Blue, Green, Grey, Red, White}
import scalafx.scene.shape.{Line, Rectangle}
import scalafx.scene.text.{Font, Text}
import tetris.ConfigGameConstants.*
import tetris.ConfigGameConstants.initialSquare.{maxShapeTypes, newShapeMap}
import tetris.State.*

import scala.annotation.tailrec
import scala.util.Random

case class State(currentShape: Shape, existingBlocks: ExistingBlocks, gameState: GameState) {

  def startGame(): State = State(currentShape, existingBlocks, GameInProgress)
  def pauseGame(): State = State(currentShape, existingBlocks, GamePaused)

  def checkIfOverThreshold(): State = {
    val updatedGameState = if (existingBlocks.squares.exists(_.yAxis < thresholdLine.startY.toDouble)) Collision else gameState
    State(currentShape, existingBlocks, updatedGameState)
  }

  def generateAllObjects: List[Rectangle] = existingBlocks.toDisplayObjects ++ currentShape.toDisplayObjects

  def moveShape(direction: MovementDirection): State =
    if (gameState == GameInProgress) {
      val newPosition = direction match {
        case Left if currentShape.leftBoundary == 0                => currentShape
        case Left                                                  => currentShape.moveLeft()
        case Right if currentShape.rightBoundary == sceneXBoundary => currentShape
        case Right                                                 => currentShape.moveRight()
      }
      if (objectCollision(newPosition))
        State(createNewShape(currentShape), existingBlocks.addShape(currentShape), gameState)
      else
        State(newPosition, existingBlocks, gameState)
    } else State(currentShape, existingBlocks, gameState)

  def rotateShape(rotation: RotationDirection): State = // TODO this and moveShape are complicated but similar. Simplify and combine?
    if (gameState == GameInProgress) {
      val newPosition            = currentShape.rotate(rotation)
      val collision              = objectCollision(newPosition)
      val newPositionOutOfBounds = newPosition.leftBoundary < 0 || newPosition.rightBoundary > sceneXBoundary
      val shape                  = if (newPositionOutOfBounds) currentShape else if (collision) createNewShape(currentShape) else newPosition
      val newExistingBlocks      = if (collision) existingBlocks.addShape(currentShape) else existingBlocks
      State(shape, newExistingBlocks, gameState)
    } else State(currentShape, existingBlocks, gameState)

  def lowerShapeOnce(): State =
    if (objectCollision(currentShape.moveDown())) State(createNewShape(currentShape), existingBlocks.addShape(currentShape), gameState)
    else State(currentShape.moveDown(), existingBlocks, gameState)

  def dropShape(): State = {
    @tailrec
    def dropShapeTillBottom(newPosition: Shape, inc: Int): ExistingBlocks = {
      val collision = objectCollision(newPosition.moveDown())
      if (objectCollision(newPosition.moveDown())) existingBlocks.addShape(newPosition)
      else dropShapeTillBottom(newPosition.moveDown(), inc + 1)
    }

    State(createNewShape(currentShape), dropShapeTillBottom(currentShape, 0), gameState)
  }

  def objectCollision(shape: Shape): Boolean = shape.buildShape.exists(existingBlocks.squares.contains(_))

  val random = new Random()
  private def createNewShape(previousShape: Shape): Shape = {
    val previousShapeType = previousShape.shapeType
    var newShapeType      = previousShapeType
    while (newShapeType == previousShapeType)
      newShapeType = random.nextInt(maxShapeTypes) + 1
    newShapeMap(newShapeType)(initialPosition, Neutral)
  }

  def displayText(): VBox =
    new VBox {
      layoutX = gameState match {
        case GameAtStart => sceneXBoundary / 4
        case Collision   => sceneXBoundary / 5
        case _           => sceneXBoundary / 3
      }
      layoutY = sceneYBoundary / 3
      alignment = Pos.Center
      children = gameState match {
        case GameAtStart => startGameText
        case Collision   => gameOverText
        case _           => pausedText
      }
      visible = gameState != GameInProgress
    }
}

object State {

  def square(objectLocation: ObjectLocation, colour: Color): Rectangle = new Rectangle {
    x = objectLocation.xAxis
    y = objectLocation.yAxis
    width = objectWidth
    height = objectWidth
    fill = colour
  }

  val thresholdLine: Line = new Line {
    startX = 0
    startY = 140
    endX = sceneXBoundary + 2 * objectWidth
    endY = 140
    stroke = Color.Red
    strokeWidth = 2
    strokeDashArray.addAll(10.0, 5.0) // Pattern: 10px dash, 5px gap
  }

  val startGameText: Text = new Text("Press SPACE to Start!") {
    fill = Black
    stroke = White
    font = Font("Ariel", 30)
  }
  val pausedText: Text = new Text("Game Paused") {
    fill = Black
    stroke = White
    font = Font("Ariel", 30)
  }
  val gameOverText: Text = new Text("GAME OVER! Press R to restart") {
    fill = Black
    stroke = White
    font = Font("Ariel", 30)
  }
}
