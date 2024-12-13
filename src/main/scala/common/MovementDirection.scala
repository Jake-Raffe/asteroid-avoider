package common

import common.ConfigGameConstants.movementAmount

trait MovementDirection {
  val xAxisMovement: Double
}

case object Right extends MovementDirection {
  val xAxisMovement: Double = movementAmount
}

case object Left extends MovementDirection {
  val xAxisMovement: Double = -movementAmount
}

case class ObjectLocation(xAxis: Double, yAxis: Double) {
  def moveLeft: ObjectLocation  = ObjectLocation(xAxis - movementAmount, yAxis)
  def moveRight: ObjectLocation = ObjectLocation(xAxis + movementAmount, yAxis)
  def moveUp: ObjectLocation    = ObjectLocation(xAxis, yAxis - movementAmount)
  def moveDown: ObjectLocation  = ObjectLocation(xAxis, yAxis + movementAmount)
}

case class HitBox(leftBoundary: Double, rightBoundary: Double)
