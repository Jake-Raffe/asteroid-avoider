import GameField.createGameField

object Main {

  def main(args: Array[String]): Unit = {
    val showShip       = true
    val numOfAsteroids = 8
    println(s"DESIGN: ShowShip - $showShip; NumOfAsteroids - $numOfAsteroids")
    println(createGameField(numOfAsteroids, showShip))
  }
}

// TODO
// Core functionality:
// - Ship must be able to move left and right
// - Work out how to display vertical scrolling
// --- In command line or in browser?
// --- How do we maintain the current display to update with next scroll? MongoDB?
// Extra features:
// -
