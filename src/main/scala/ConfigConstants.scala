object ConfigConstants {

  val objectWidth: Double = 25

  // TODO configure window creation to match scene boundary
  val sceneXBoundary: Double = 600
  val sceneYBoundary: Double = 600

  private val shipStartXPosition: Double = sceneXBoundary / 2
  val shipStartPosition: ObjectLocation  = ObjectLocation(shipStartXPosition, sceneYBoundary)

  val scrollSpeed: Long      = 800
  val movementAmount: Double = objectWidth

  val initialState: State = State(
    shipStartPosition,
    List.empty[ObjectLocation],
    gameInMotion = false,
    collision = false
  ) // TODO why does it not change state if it starts with no asteroids?
  val initialStateWithAsteroids: State =
    State(shipStartPosition, List(ObjectLocation(0, 0), ObjectLocation(110, 110), ObjectLocation(200, 70)), gameInMotion = false, collision = false)

  val frameScrollMap: Map[Int, Long] =
    Map(5 -> 100, 10 -> 200, 15 -> 300, 20 -> 400, 25 -> 500, 30 -> 600, 35 -> 700, 40 -> 720, 45 -> 740, 50 -> 760)
}
