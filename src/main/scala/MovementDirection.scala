import ConfigConstants.movementAmount

trait MovementDirection {
  val xAxisMovement: Double
}

case object Right extends MovementDirection {
  val xAxisMovement: Double = movementAmount
}

case object Left extends MovementDirection {
  val xAxisMovement: Double = -movementAmount
}

case class ObjectLocation(xAxis: Double, yAxis: Double)
