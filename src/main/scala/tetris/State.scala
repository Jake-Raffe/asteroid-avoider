package tetris

import asteroidAvoider.ConfigGameConstants.*
import asteroidAvoider.State.{shipHasCrashed, square}
import scalafx.scene.text.Text
import scalafx.scene.text.Font
import scalafx.geometry.Pos
import scalafx.scene.layout.VBox
import common.*
import common.ConfigGameConstants.objectWidth
import scalafx.beans.property.BooleanProperty
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Black, Grey, Red, White}
import scalafx.scene.shape.Rectangle
import tetris.ConfigGameConstants.initialSquare.{maxShapeTypes, newShapeMap}
import tetris.ConfigGameConstants.{initialPosition, initialSquare}

import scala.annotation.tailrec
import scala.util.Random

case class State(currentShape: Shape, existingBlocks: ExistingBlocks, gameState: GameState) {

  def startGame(): State = State(currentShape, existingBlocks, GameInProgress)
  def pauseGame(): State = State(currentShape, existingBlocks, GamePaused)

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
    newShapeMap(newShapeType)(initialPosition)
  }

  val pausedDisplayBox: VBox = {
    val pausedText: Text = new Text("Game Paused") {
      fill = White
      font = Font("Ariel", 30)
    }
    new VBox {
      alignment = Pos.Center
      children = pausedText
      visible <== BooleanProperty(gameState == GamePaused)
    }
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
}
