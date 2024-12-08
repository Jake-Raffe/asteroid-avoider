object ConfigConstants {

  val objectWidth: Double = 25

  val sceneXBoundary: Double = 600
  val sceneYBoundary: Double = 600

  private val shipStartXPosition: Double = sceneXBoundary / 2
  val shipYPosition: Double              = sceneYBoundary - (2 * objectWidth)
  val shipStartPosition: ObjectLocation  = ObjectLocation(shipStartXPosition, shipYPosition)

  val scrollSpeed: Long      = 800
  val movementAmount: Double = objectWidth

  val initialState: State = State(shipStartPosition, List.empty[ObjectLocation], gameInMotion = false, collision = false)
  val initialStateWithAsteroids: State =
    State(shipStartPosition, List(ObjectLocation(0, 0), ObjectLocation(110, 110), ObjectLocation(200, 70)), gameInMotion = false, collision = false)

  val frameScrollMap: Map[Int, Long] =
    Map(
      5   -> 100,
      10  -> 200,
      15  -> 250,
      20  -> 300,
      25  -> 350,
      30  -> 375,
      40  -> 400,
      50  -> 425,
      60  -> 450,
      80  -> 460,
      100 -> 470,
      120 -> 480,
      140 -> 490,
      160 -> 500,
      200 -> 510,
      250 -> 525,
      300 -> 550)
}
