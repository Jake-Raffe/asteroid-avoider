object ConfigConstants {

  val objectWidth: Double = 25

  val sceneXBoundary: Double = 600 // TODO configure window creation to match scene boundary
  val sceneYBoundary: Double = 600

  private val shipStartXPosition: Double = sceneXBoundary / 2
  val shipStartPosition: ObjectLocation  = ObjectLocation(shipStartXPosition, sceneYBoundary)

  val scrollSpeed: Long      = 1000
  val movementAmount: Double = objectWidth

  val initialState: State = State(shipStartPosition, List.empty[ObjectLocation])
}
