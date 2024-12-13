package tetris

import common.*
import common.ConfigGameConstants.objectWidth
import scalafx.scene.paint.Color.{Green, Grey, Silver, White}
import scalafx.scene.shape.Rectangle
import tetris.ConfigGameConstants.{sceneXBoundary, sceneYBoundary}
import tetris.State.square

import scala.annotation.tailrec

trait Shape {
  val shapeType: Int
  val maxShapeTypes: Int = 8
  val centralPoint: ObjectLocation
  val lowerBoundary: ObjectLocation
  val leftBoundary: Double
  val rightBoundary: Double
  val buildShape: List[ObjectLocation]
  val toDisplayObjects: List[Rectangle]

  def moveLeft(): this.type  = createNewShape(centralPoint.moveLeft).asInstanceOf[this.type]
  def moveRight(): this.type = createNewShape(centralPoint.moveRight).asInstanceOf[this.type]
  def moveDown(): this.type  = createNewShape(centralPoint.moveDown).asInstanceOf[this.type]
  protected def createNewShape(newCentralPoint: ObjectLocation): Shape

  def move(direction: MovementDirection): Shape = direction match {
    case Left  => moveLeft()
    case Right => moveRight()
  }

  val newShapeMap: Map[Int, ObjectLocation => Shape] =
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

case class Square(centralPoint: ObjectLocation) extends Shape {
  override val shapeType                         = 1
  override val buildShape: List[ObjectLocation]  = List(centralPoint, centralPoint.moveLeft, centralPoint.moveUp, centralPoint.moveLeft.moveUp)
  override val lowerBoundary: ObjectLocation     = centralPoint.moveDown
  override val leftBoundary: Double              = centralPoint.moveLeft.xAxis
  override val rightBoundary: Double             = centralPoint.moveRight.xAxis
  override val toDisplayObjects: List[Rectangle] = buildShape.map(square(_, Green))

  override protected def createNewShape(newCentralPoint: ObjectLocation): Square =
    Square(newCentralPoint)
}

case class Line(centralPoint: ObjectLocation) extends Shape {
  override val shapeType                         = 2
  override val buildShape: List[ObjectLocation]  = List(centralPoint, centralPoint.moveUp, centralPoint.moveUp.moveUp, centralPoint.moveDown)
  override val lowerBoundary: ObjectLocation     = centralPoint.moveDown.moveDown
  override val leftBoundary: Double              = centralPoint.xAxis
  override val rightBoundary: Double             = centralPoint.moveRight.xAxis
  override val toDisplayObjects: List[Rectangle] = buildShape.map(square(_, Green))

  override protected def createNewShape(newCentralPoint: ObjectLocation): Line =
    Line(newCentralPoint)
}

case class RectangleShape(centralPoint: ObjectLocation) extends Shape {
  override val shapeType = 3
  override val buildShape: List[ObjectLocation] = List(
    centralPoint,
    centralPoint.moveLeft,
    centralPoint.moveUp,
    centralPoint.moveLeft.moveUp,
    centralPoint.moveUp.moveUp,
    centralPoint.moveUp.moveLeft.moveUp)
  override val lowerBoundary: ObjectLocation     = centralPoint.moveDown
  override val leftBoundary: Double              = centralPoint.moveLeft.xAxis
  override val rightBoundary: Double             = centralPoint.moveRight.xAxis
  override val toDisplayObjects: List[Rectangle] = buildShape.map(square(_, Green))

  override protected def createNewShape(newCentralPoint: ObjectLocation): RectangleShape =
    RectangleShape(newCentralPoint)
}

case class LShape(centralPoint: ObjectLocation) extends Shape {
  override val shapeType = 4
  override val buildShape: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft, centralPoint.moveLeft.moveUp, centralPoint.moveLeft.moveUp.moveUp)
  override val lowerBoundary: ObjectLocation     = centralPoint.moveDown
  override val leftBoundary: Double              = centralPoint.moveLeft.xAxis
  override val rightBoundary: Double             = centralPoint.moveRight.xAxis
  override val toDisplayObjects: List[Rectangle] = buildShape.map(square(_, Green))

  override protected def createNewShape(newCentralPoint: ObjectLocation): LShape =
    LShape(newCentralPoint)
}

case class LShapeRev(centralPoint: ObjectLocation) extends Shape {
  override val shapeType = 5
  override val buildShape: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft, centralPoint.moveUp, centralPoint.moveUp.moveUp)
  override val lowerBoundary: ObjectLocation     = centralPoint.moveDown
  override val leftBoundary: Double              = centralPoint.moveLeft.xAxis
  override val rightBoundary: Double             = centralPoint.moveRight.xAxis
  override val toDisplayObjects: List[Rectangle] = buildShape.map(square(_, Green))

  override protected def createNewShape(newCentralPoint: ObjectLocation): LShapeRev =
    LShapeRev(newCentralPoint)
}

case class Zigzag(centralPoint: ObjectLocation) extends Shape {
  override val shapeType = 6
  override val buildShape: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveRight, centralPoint.moveUp, centralPoint.moveLeft.moveUp)
  override val lowerBoundary: ObjectLocation     = centralPoint.moveDown
  override val leftBoundary: Double              = centralPoint.moveLeft.xAxis
  override val rightBoundary: Double             = centralPoint.moveRight.moveRight.xAxis
  override val toDisplayObjects: List[Rectangle] = buildShape.map(square(_, Green))

  override protected def createNewShape(newCentralPoint: ObjectLocation): Zigzag =
    Zigzag(newCentralPoint)
}

case class ZigzagRev(centralPoint: ObjectLocation) extends Shape {
  override val shapeType = 7
  override val buildShape: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveLeft, centralPoint.moveUp, centralPoint.moveRight.moveUp)
  override val lowerBoundary: ObjectLocation     = centralPoint.moveDown
  override val leftBoundary: Double              = centralPoint.moveLeft.xAxis
  override val rightBoundary: Double             = centralPoint.moveRight.moveRight.xAxis
  override val toDisplayObjects: List[Rectangle] = buildShape.map(square(_, Green))

  override protected def createNewShape(newCentralPoint: ObjectLocation): ZigzagRev =
    ZigzagRev(newCentralPoint)
}

case class TShape(centralPoint: ObjectLocation) extends Shape {
  override val shapeType = 8
  override val buildShape: List[ObjectLocation] =
    List(centralPoint, centralPoint.moveUp, centralPoint.moveUp.moveLeft, centralPoint.moveUp.moveRight)
  override val lowerBoundary: ObjectLocation     = centralPoint.moveDown
  override val leftBoundary: Double              = centralPoint.moveLeft.xAxis
  override val rightBoundary: Double             = centralPoint.moveRight.moveRight.xAxis
  override val toDisplayObjects: List[Rectangle] = buildShape.map(square(_, Green))

  override protected def createNewShape(newCentralPoint: ObjectLocation): TShape =
    TShape(newCentralPoint)
}

case class ExistingBlocks(squares: List[ObjectLocation]) {
  def addShape(newShape: Shape): ExistingBlocks = ExistingBlocks(squares ++ newShape.buildShape)
  val toDisplayObjects: List[Rectangle]         = squares.map(s => square(s, if (s.yAxis == sceneYBoundary) Grey else Silver))
}

object LowerBoundary {
  private def createBoundary(): List[ObjectLocation] = {
    @tailrec
    def addNextSquare(existingSquares: List[ObjectLocation], boundary: Double): List[ObjectLocation] =
      if (boundary == sceneXBoundary + (2 * objectWidth)) existingSquares
      else
        addNextSquare(existingSquares.appended(ObjectLocation(boundary, sceneYBoundary)), boundary + objectWidth)
    addNextSquare(List(ObjectLocation(0, sceneYBoundary)), 0 + objectWidth)
  }

  val squares: List[ObjectLocation] = createBoundary()
}
