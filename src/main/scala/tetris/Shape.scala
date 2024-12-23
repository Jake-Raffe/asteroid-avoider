package tetris

import common.*
import common.ConfigGameConstants.objectWidth
import scalafx.scene.paint.Color.{Green, Grey, Silver, White}
import scalafx.scene.shape.Rectangle
import tetris.ConfigGameConstants.{sceneXBoundary, sceneYBoundary, stageXBoundary}
import tetris.State.square

import scala.annotation.tailrec

trait Shape {
  val maxShapeTypes: Int = 8
  val shapeType: Int
  def centralPoint: ObjectLocation
  def orientation: Orientation
  def lowerBoundary: ObjectLocation
  def leftBoundary: Double
  def rightBoundary: Double

  def toDisplayObjects: List[Rectangle] = buildShape.map(square(_, Green))

  def neutralPosition: List[ObjectLocation]
  def clockwisePosition: List[ObjectLocation]
  def anticlockwisePosition: List[ObjectLocation]
  def inversePosition: List[ObjectLocation]
  def buildShape: List[ObjectLocation] = orientation match {
    case Neutral       => neutralPosition
    case Clockwise     => clockwisePosition
    case AntiClockwise => anticlockwisePosition
    case Inverse       => inversePosition
  }

  def moveLeft(): this.type  = createNewShape(centralPoint.moveLeft, orientation).asInstanceOf[this.type]
  def moveRight(): this.type = createNewShape(centralPoint.moveRight, orientation).asInstanceOf[this.type]
  def moveDown(): this.type  = createNewShape(centralPoint.moveDown, orientation).asInstanceOf[this.type]

  def rotate(rotation: RotationDirection): this.type = {
    val newOrientation = rotation match {
      case ClockwiseRotate     => orientation.rotateClockwise
      case AntiClockwiseRotate => orientation.rotateAntiClockwise
    }
    createNewShape(centralPoint, newOrientation).asInstanceOf[this.type]
  }

  protected def createNewShape(newCentralPoint: ObjectLocation, newOrientation: Orientation): Shape

  val newShapeMap: Map[Int, (ObjectLocation, Orientation) => Shape] =
    Map(
      1 -> Square.apply,
      2 -> Line.apply,
      3 -> RectangleShape.apply,
      4 -> LShape.apply,
      5 -> LShapeRev.apply,
      6 -> Zigzag.apply,
      7 -> ZigzagRev.apply,
      8 -> TShape.apply)
}

case class Square(centralPoint: ObjectLocation, orientation: Orientation) extends Shape {
  override val shapeType                               = 1
  val shape: List[ObjectLocation]                      = List(centralPoint, centralPoint.moveLeft, centralPoint.moveUp, centralPoint.moveLeft.moveUp)
  override def neutralPosition: List[ObjectLocation]   = shape
  override def clockwisePosition: List[ObjectLocation] = shape
  override def anticlockwisePosition: List[ObjectLocation] = shape
  override def inversePosition: List[ObjectLocation]       = shape
  override def lowerBoundary: ObjectLocation               = centralPoint.moveDown
  override def leftBoundary: Double                        = centralPoint.moveLeft.xAxis
  override def rightBoundary: Double                       = centralPoint.moveRight.xAxis

  override protected def createNewShape(newCentralPoint: ObjectLocation, newOrientation: Orientation): Square =
    Square(newCentralPoint, newOrientation)
}

case class Line(centralPoint: ObjectLocation, orientation: Orientation) extends Shape {
  override val shapeType                             = 2
  override def neutralPosition: List[ObjectLocation] = List(centralPoint, centralPoint.moveUp, centralPoint.moveUp.moveUp, centralPoint.moveDown)
  override def clockwisePosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveRight, centralPoint.moveLeft.moveLeft, centralPoint.moveLeft)
  override def anticlockwisePosition: List[ObjectLocation] =
    List(centralPoint.moveUp, centralPoint.moveUp.moveRight, centralPoint.moveUp.moveLeft.moveLeft, centralPoint.moveUp.moveLeft)
  override def inversePosition: List[ObjectLocation] =
    List(centralPoint.moveLeft, centralPoint.moveLeft.moveDown, centralPoint.moveLeft.moveUp, centralPoint.moveLeft.moveUp.moveUp)

  override def lowerBoundary: ObjectLocation = orientation match {
    case Neutral | Inverse => centralPoint.moveDown.moveDown
    case Clockwise         => centralPoint.moveDown
    case AntiClockwise     => centralPoint
  }
  override def leftBoundary: Double = orientation match {
    case Neutral                   => centralPoint.xAxis
    case Clockwise | AntiClockwise => centralPoint.moveLeft.moveLeft.xAxis
    case Inverse                   => centralPoint.moveLeft.xAxis
  }
  override def rightBoundary: Double = orientation match {
    case Neutral                   => centralPoint.moveRight.xAxis
    case Clockwise | AntiClockwise => centralPoint.moveRight.moveRight.xAxis
    case Inverse                   => centralPoint.xAxis
  }

  override protected def createNewShape(newCentralPoint: ObjectLocation, newOrientation: Orientation): Line =
    Line(newCentralPoint, newOrientation)
}

case class RectangleShape(centralPoint: ObjectLocation, orientation: Orientation) extends Shape {
  override val shapeType = 3
  override def neutralPosition: List[ObjectLocation] = List(
    centralPoint,
    centralPoint.moveLeft,
    centralPoint.moveUp,
    centralPoint.moveLeft.moveUp,
    centralPoint.moveUp.moveUp,
    centralPoint.moveUp.moveUp.moveLeft)
  override def clockwisePosition: List[ObjectLocation] = List(
    centralPoint,
    centralPoint.moveLeft,
    centralPoint.moveUp,
    centralPoint.moveUp.moveLeft,
    centralPoint.moveRight,
    centralPoint.moveRight.moveUp)
  override def anticlockwisePosition: List[ObjectLocation] = List(
    centralPoint,
    centralPoint.moveLeft,
    centralPoint.moveUp,
    centralPoint.moveUp.moveLeft,
    centralPoint.moveLeft.moveLeft,
    centralPoint.moveLeft.moveLeft.moveUp
  )
  override def inversePosition: List[ObjectLocation] = List(
    centralPoint,
    centralPoint.moveLeft,
    centralPoint.moveUp,
    centralPoint.moveLeft.moveUp,
    centralPoint.moveDown,
    centralPoint.moveDown.moveLeft)
  override def lowerBoundary: ObjectLocation = orientation match {
    case Neutral | Clockwise | AntiClockwise => centralPoint.moveDown
    case Inverse                             => centralPoint.moveDown.moveDown
  }
  override def leftBoundary: Double = orientation match {
    case Neutral | Clockwise | Inverse => centralPoint.moveLeft.xAxis
    case AntiClockwise                 => centralPoint.moveLeft.moveLeft.xAxis
  }
  override def rightBoundary: Double = orientation match {
    case Neutral | AntiClockwise | Inverse => centralPoint.moveRight.xAxis
    case Clockwise                         => centralPoint.moveRight.moveRight.xAxis
  }

  override protected def createNewShape(newCentralPoint: ObjectLocation, newOrientation: Orientation): RectangleShape =
    RectangleShape(newCentralPoint, newOrientation)
}

case class LShape(centralPoint: ObjectLocation, orientation: Orientation) extends Shape {
  override val shapeType = 4
  override def neutralPosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft, centralPoint.moveLeft.moveUp, centralPoint.moveLeft.moveUp.moveUp)
  override def clockwisePosition: List[ObjectLocation] =
    List(centralPoint.moveLeft, centralPoint.moveLeft.moveUp, centralPoint.moveUp, centralPoint.moveUp.moveRight)
  override def anticlockwisePosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft, centralPoint.moveLeft.moveLeft, centralPoint.moveUp)
  override def inversePosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveDown, centralPoint.moveUp, centralPoint.moveLeft.moveUp)
  override def lowerBoundary: ObjectLocation = orientation match {
    case Neutral | Clockwise | AntiClockwise => centralPoint.moveDown
    case Inverse                             => centralPoint.moveDown.moveDown
  }
  override def leftBoundary: Double = orientation match {
    case Neutral | Clockwise | Inverse => centralPoint.moveLeft.xAxis
    case AntiClockwise                 => centralPoint.moveLeft.moveLeft.xAxis
  }
  override def rightBoundary: Double = orientation match {
    case Neutral | AntiClockwise | Inverse => centralPoint.moveRight.xAxis
    case Clockwise                         => centralPoint.moveRight.moveRight.xAxis
  }

  override protected def createNewShape(newCentralPoint: ObjectLocation, newOrientation: Orientation): LShape =
    LShape(newCentralPoint, newOrientation)
}

case class LShapeRev(centralPoint: ObjectLocation, orientation: Orientation) extends Shape {
  override val shapeType = 5
  override def neutralPosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft, centralPoint.moveUp, centralPoint.moveUp.moveUp)
  override def clockwisePosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft, centralPoint.moveLeft.moveUp, centralPoint.moveRight)
  override def anticlockwisePosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft.moveUp, centralPoint.moveUp, centralPoint.moveLeft.moveLeft.moveUp)
  override def inversePosition: List[ObjectLocation] =
    List(centralPoint.moveLeft, centralPoint.moveLeft.moveDown, centralPoint.moveUp, centralPoint.moveLeft.moveUp)
  override def lowerBoundary: ObjectLocation = orientation match {
    case Neutral | Clockwise | AntiClockwise => centralPoint.moveDown
    case Inverse                             => centralPoint.moveDown.moveDown
  }
  override def leftBoundary: Double = orientation match {
    case Neutral | Clockwise | Inverse => centralPoint.moveLeft.xAxis
    case AntiClockwise                 => centralPoint.moveLeft.moveLeft.xAxis
  }
  override def rightBoundary: Double = orientation match {
    case Neutral | AntiClockwise | Inverse => centralPoint.moveRight.xAxis
    case Clockwise                         => centralPoint.moveRight.moveRight.xAxis
  }

  override protected def createNewShape(newCentralPoint: ObjectLocation, newOrientation: Orientation): LShapeRev =
    LShapeRev(newCentralPoint, newOrientation)
}

case class Zigzag(centralPoint: ObjectLocation, orientation: Orientation) extends Shape {
  override val shapeType = 6
  override def neutralPosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveRight, centralPoint.moveUp, centralPoint.moveLeft.moveUp)
  override def clockwisePosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft, centralPoint.moveUp, centralPoint.moveLeft.moveDown)
  override def anticlockwisePosition: List[ObjectLocation] =
    List(centralPoint.moveUp, centralPoint.moveUp.moveUp, centralPoint.moveUp.moveLeft, centralPoint.moveLeft)
  override def inversePosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft, centralPoint.moveLeft.moveUp, centralPoint.moveLeft.moveLeft.moveUp)
  override def lowerBoundary: ObjectLocation = orientation match {
    case Neutral | Inverse | AntiClockwise => centralPoint.moveDown
    case Clockwise                         => centralPoint.moveDown.moveDown
  }
  override def leftBoundary: Double = orientation match {
    case Neutral | Clockwise | AntiClockwise => centralPoint.moveLeft.xAxis
    case Inverse                             => centralPoint.moveLeft.moveLeft.xAxis
  }
  override def rightBoundary: Double = orientation match {
    case Clockwise | AntiClockwise | Inverse => centralPoint.moveRight.xAxis
    case Neutral                             => centralPoint.moveRight.moveRight.xAxis
  }

  override protected def createNewShape(newCentralPoint: ObjectLocation, newOrientation: Orientation): Zigzag =
    Zigzag(newCentralPoint, newOrientation)
}

case class ZigzagRev(centralPoint: ObjectLocation, orientation: Orientation) extends Shape {
  override val shapeType = 7
  override def neutralPosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft, centralPoint.moveUp, centralPoint.moveRight.moveUp)
  override def clockwisePosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft, centralPoint.moveLeft.moveUp, centralPoint.moveDown)
  override def anticlockwisePosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveUp, centralPoint.moveUp.moveLeft, centralPoint.moveUp.moveUp.moveLeft)
  override def inversePosition: List[ObjectLocation] =
    List(centralPoint.moveLeft, centralPoint.moveLeft.moveLeft, centralPoint.moveLeft.moveUp, centralPoint.moveUp)
  override def lowerBoundary: ObjectLocation = orientation match {
    case Neutral | Inverse | AntiClockwise => centralPoint.moveDown
    case Clockwise                         => centralPoint.moveDown.moveDown
  }
  override def leftBoundary: Double = orientation match {
    case Neutral | Clockwise | AntiClockwise => centralPoint.moveLeft.xAxis
    case Inverse                             => centralPoint.moveLeft.moveLeft.xAxis
  }
  override def rightBoundary: Double = orientation match {
    case Clockwise | AntiClockwise | Inverse => centralPoint.moveRight.xAxis
    case Neutral                             => centralPoint.moveRight.moveRight.xAxis
  }

  override protected def createNewShape(newCentralPoint: ObjectLocation, newOrientation: Orientation): ZigzagRev =
    ZigzagRev(newCentralPoint, newOrientation)
}

case class TShape(centralPoint: ObjectLocation, orientation: Orientation) extends Shape {
  override val shapeType = 8
  override def neutralPosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveUp, centralPoint.moveUp.moveLeft, centralPoint.moveUp.moveRight)
  override def clockwisePosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft, centralPoint.moveUp, centralPoint.moveDown)
  override def anticlockwisePosition: List[ObjectLocation] =
    List(centralPoint.moveLeft, centralPoint.moveUp, centralPoint.moveUp.moveLeft, centralPoint.moveUp.moveUp.moveLeft)
  override def inversePosition: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft.moveLeft, centralPoint.moveLeft.moveUp, centralPoint.moveLeft)
  override def lowerBoundary: ObjectLocation = orientation match {
    case Neutral | Inverse | AntiClockwise => centralPoint.moveDown
    case Clockwise                         => centralPoint.moveDown.moveDown
  }
  override def leftBoundary: Double = orientation match {
    case Neutral | Clockwise | AntiClockwise => centralPoint.moveLeft.xAxis
    case Inverse                             => centralPoint.moveLeft.moveLeft.xAxis
  }
  override def rightBoundary: Double = orientation match {
    case Clockwise | AntiClockwise | Inverse => centralPoint.moveRight.xAxis
    case Neutral                             => centralPoint.moveRight.moveRight.xAxis
  }

  override protected def createNewShape(newCentralPoint: ObjectLocation, newOrientation: Orientation): TShape =
    TShape(newCentralPoint, newOrientation)
}

case class ExistingBlocks(squares: List[ObjectLocation]) {

  val existingPlusFloor: List[ObjectLocation] = squares ++ LowerBoundary.createBoundary()

  def addShape(newShape: Shape): ExistingBlocks = ExistingBlocks(squares ++ newShape.buildShape).removeRowIfFull()

  def removeRowIfFull(): ExistingBlocks = {
    val blocksNeededForFullRow     = stageXBoundary / objectWidth
    val blocksListInAscendingOrder = squares.sortBy(-_.yAxis)

    @tailrec
    def checkIfNextRowIsFull(remainingBlocksToCheck: List[ObjectLocation], checkedBlocks: List[ObjectLocation]): List[ObjectLocation] =
      remainingBlocksToCheck match {
        case head :: tail =>
          val (blocksInRow, otherBlocks) = tail.partition(_.yAxis == head.yAxis) match {
            case (same, diff) => (head :: same, diff)
          }
          val rowIsFull = blocksInRow.length >= blocksNeededForFullRow
          if (rowIsFull) {
            checkIfNextRowIsFull(remainingBlocksToCheck = otherBlocks.map(_.moveDown), checkedBlocks)
          } else {
            checkIfNextRowIsFull(remainingBlocksToCheck = otherBlocks, blocksInRow ++ checkedBlocks)
          }
        case Nil => checkedBlocks
      }

    ExistingBlocks(checkIfNextRowIsFull(blocksListInAscendingOrder, List.empty[ObjectLocation]))
  }

  val toDisplayObjects: List[Rectangle] = squares.map(square(_, Silver))
}

object LowerBoundary {

  def createBoundary(): List[ObjectLocation] = {
    @tailrec
    def addNextSquare(existingSquares: List[ObjectLocation], boundary: Double): List[ObjectLocation] =
      if (boundary == stageXBoundary) existingSquares
      else
        addNextSquare(existingSquares.appended(ObjectLocation(boundary, sceneYBoundary)), boundary + objectWidth)
    addNextSquare(List(ObjectLocation(0, sceneYBoundary)), 0 + objectWidth)
  }

  val toDisplayObjects: List[Rectangle] = createBoundary().map(square(_, Grey))
}

trait Orientation {
  val rotateClockwise: Orientation
  val rotateAntiClockwise: Orientation
}
object Neutral extends Orientation {
  override val rotateClockwise: Orientation     = Clockwise
  override val rotateAntiClockwise: Orientation = AntiClockwise
}
object Clockwise extends Orientation {
  override val rotateClockwise: Orientation     = Inverse
  override val rotateAntiClockwise: Orientation = Neutral
}
object AntiClockwise extends Orientation {
  override val rotateClockwise: Orientation     = Neutral
  override val rotateAntiClockwise: Orientation = Inverse
}
object Inverse extends Orientation {
  override val rotateClockwise: Orientation     = AntiClockwise
  override val rotateAntiClockwise: Orientation = Clockwise
}
