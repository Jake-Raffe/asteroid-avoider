package tetris

import asteroidAvoider.State.square
import common.*
import common.ConfigGameConstants.objectWidth
import scalafx.scene.paint.Color.{Grey, White}
import scalafx.scene.shape.Rectangle

trait Shape {
  def move(direction: MovementDirection): Shape = direction match {
    case Left  => moveLeft()
    case Right => moveRight()
  }
  def moveLeft(): Shape
  def moveRight(): Shape
  def moveDown(): Shape
  val toDisplayObjects: List[Rectangle]
  val lowerBoundary: ObjectLocation
  val leftBoundary: Double
  val rightBoundary: Double
  val buildShape: List[ObjectLocation]
  val centerPoint: ObjectLocation
}

case class Square(centralPoint: ObjectLocation) extends Shape {
  val buildShape: List[ObjectLocation] = List(centralPoint, centralPoint.moveLeft, centralPoint.moveUp, centralPoint.moveLeft.moveUp)
  val centerPoint: ObjectLocation = centralPoint
  val toDisplayObjects: List[Rectangle] = buildShape.map(square(_, White))
  override val lowerBoundary: ObjectLocation = centralPoint.moveDown
  override val leftBoundary: Double = centralPoint.moveLeft.xAxis
  override val rightBoundary: Double = centralPoint.moveRight.xAxis
  
  def moveLeft(): Shape                        = Square(centralPoint.moveLeft)
  def moveRight(): Shape                       = Square(centralPoint.moveRight)
  def moveDown(): Shape                       = Square(centralPoint.moveDown)
}

case class ExistingBlocks(squares: List[ObjectLocation]) {
  def addShape(newShape: Shape): ExistingBlocks = ExistingBlocks(squares ++ newShape.buildShape)
  val toDisplayObjects: List[Rectangle] = squares.map(square(_, Grey))
}
