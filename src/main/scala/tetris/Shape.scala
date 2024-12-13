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
  val maxShapeTypes: Int = 2
  val centralPoint: ObjectLocation
  val lowerBoundary: ObjectLocation
  val leftBoundary: Double
  val rightBoundary: Double
  val buildShape: List[ObjectLocation]
  val toDisplayObjects: List[Rectangle]

  def moveLeft(): Shape
  def moveRight(): Shape
  def moveDown(): Shape

  def move(direction: MovementDirection): Shape = direction match {
    case Left  => moveLeft()
    case Right => moveRight()
  }

  val newShapeMap: Map[Int, ObjectLocation => Shape] = Map(1 -> Square, 2 -> Line)
}

case class Square(centralPoint: ObjectLocation) extends Shape {
  override val shapeType                         = 1
  override val buildShape: List[ObjectLocation]  = List(centralPoint, centralPoint.moveLeft, centralPoint.moveUp, centralPoint.moveLeft.moveUp)
  override val lowerBoundary: ObjectLocation     = centralPoint.moveDown
  override val leftBoundary: Double              = centralPoint.moveLeft.xAxis
  override val rightBoundary: Double             = centralPoint.moveRight.xAxis
  override val toDisplayObjects: List[Rectangle] = buildShape.map(square(_, Green))
  override def moveLeft(): Shape                 = Square(centralPoint.moveLeft)
  override def moveRight(): Shape                = Square(centralPoint.moveRight)
  override def moveDown(): Shape                 = Square(centralPoint.moveDown)
}

case class Line(centralPoint: ObjectLocation) extends Shape {
  override val shapeType                         = 2
  override val buildShape: List[ObjectLocation]  = List(centralPoint, centralPoint.moveUp, centralPoint.moveUp.moveUp, centralPoint.moveDown)
  override val lowerBoundary: ObjectLocation     = centralPoint.moveDown.moveDown
  override val leftBoundary: Double              = centralPoint.xAxis
  override val rightBoundary: Double             = centralPoint.moveRight.xAxis
  override val toDisplayObjects: List[Rectangle] = buildShape.map(square(_, Green))
  override def moveLeft(): Shape                 = Line(centralPoint.moveLeft)
  override def moveRight(): Shape                = Line(centralPoint.moveRight)
  override def moveDown(): Shape                 = Line(centralPoint.moveDown)
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
