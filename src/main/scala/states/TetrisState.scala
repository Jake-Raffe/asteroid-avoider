package states

import common.*
import configs.TetrisConfig.*
import scalafx.beans.property.BooleanProperty
import scalafx.geometry.Pos
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Black, Grey, White}
import scalafx.scene.shape.{Line, Rectangle}
import scalafx.scene.text.{Font, Text}
import states.TetrisState.thresholdLine
import games.Tetris.*

import scala.annotation.tailrec
import scala.util.Random

case class TetrisState(currentShape: TetrisShape, existingBlocks: ExistingBlocks, gameState: GameState) extends State {
  override val currentGameState: GameState = this.gameState

  def startGame(): TetrisState = TetrisState(currentShape, existingBlocks, GameInProgress)
  def pauseGame(): TetrisState = TetrisState(currentShape, existingBlocks, GamePaused)

  def checkIfOverThreshold(): TetrisState = {
    val updatedGameState = if (existingBlocks.squares.exists(_.yAxis < thresholdLine.startY.toDouble)) Collision else gameState
    TetrisState(currentShape, existingBlocks, updatedGameState)
  }

  def generateAllObjects: List[Rectangle] = LowerBoundary.toDisplayObjects ++ existingBlocks.toDisplayObjects ++ currentShape.toDisplayObjects

  def moveShape(direction: MovementDirection): TetrisState =
    if (gameState == GameInProgress) {
      val newPosition = direction match {
        case Left if currentShape.leftBoundary == 0                => currentShape
        case Left                                                  => currentShape.moveLeft()
        case Right if currentShape.rightBoundary == stageXBoundary => currentShape
        case Right                                                 => currentShape.moveRight()
      }
      if (objectCollision(newPosition))
        TetrisState(createNewShape(currentShape), existingBlocks.addShape(currentShape), gameState)
      else
        TetrisState(newPosition, existingBlocks, gameState)
    } else TetrisState(currentShape, existingBlocks, gameState)

  def rotateShape(rotation: RotationDirection): TetrisState = // TODO this and moveShape are complicated but similar. Simplify and combine?
    if (gameState == GameInProgress) {
      val newPosition            = currentShape.rotate(rotation)
      val collision              = objectCollision(newPosition)
      val newPositionOutOfBounds = newPosition.leftBoundary < 0 || newPosition.rightBoundary > stageXBoundary
      val shape                  = if (newPositionOutOfBounds) currentShape else if (collision) createNewShape(currentShape) else newPosition
      val newExistingBlocks      = if (collision) existingBlocks.addShape(currentShape) else existingBlocks
      TetrisState(shape, newExistingBlocks, gameState)
    } else TetrisState(currentShape, existingBlocks, gameState)

  def lowerShapeOnce(): TetrisState =
    if (objectCollision(currentShape.moveDown())) TetrisState(createNewShape(currentShape), existingBlocks.addShape(currentShape), gameState)
    else TetrisState(currentShape.moveDown(), existingBlocks, gameState)

  def dropShape(): TetrisState = {
    @tailrec
    def dropShapeTillBottom(newPosition: TetrisShape, inc: Int): ExistingBlocks = {
      val collision = objectCollision(newPosition.moveDown())
      if (objectCollision(newPosition.moveDown())) existingBlocks.addShape(newPosition)
      else dropShapeTillBottom(newPosition.moveDown(), inc + 1)
    }

    TetrisState(createNewShape(currentShape), dropShapeTillBottom(currentShape, 0), gameState)
  }

  private def objectCollision(shape: TetrisShape): Boolean = shape.buildShape.exists(existingBlocks.existingPlusFloor.contains(_))

  val random = new Random()
  private def createNewShape(previousShape: TetrisShape): TetrisShape = {
    val previousShapeType = previousShape.shapeType
    var newShapeType      = previousShapeType
    while (newShapeType == previousShapeType)
      newShapeType = random.nextInt(maxShapeTypes) + 1
    newShapeMap(newShapeType)(initialPosition, Neutral)
  }

  def displayText(score: Int): VBox = {
    val text = createTextWithScore(gameState, score / 5)
    new VBox {
      layoutX = objectWidth + (sceneXBoundary - text.boundsInLocal().getWidth) / 2
      layoutY = sceneYBoundary / 3
      alignment = Pos.Center
      children = text
      visible = gameState != GameInProgress
    }
  }
}

object TetrisState {

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
    endX = stageXBoundary
    endY = 140
    stroke = Color.Red
    strokeWidth = 2
    strokeDashArray.addAll(10.0, 5.0) // Pattern: 10px dash, 5px gap
  }
}
