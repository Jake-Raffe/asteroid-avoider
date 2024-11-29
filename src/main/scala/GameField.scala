import scala.annotation.tailrec
import scala.util.Random

object GameField {

  private val emptySpaceshipRow: String = """         /\         """ + "\n" + """       </  \>       """

  private val emptySpaceRow: String = """....................""" + "\n" + """...................."""

  private val spaceRow1: String = """.........../\.......""" + "\n" + """...........\/......."""

  private val spaceRow2: String = """................../\""" + "\n" + """..................\/"""

  private val spaceRow3: String = """.../\...............""" + "\n" + """...\/..............."""

  private val spaceRowMap: Map[Int, String] = Map(
    0 -> spaceRow1,
    1 -> spaceRow2,
    2 -> spaceRow3
  )

  def createGameField(numOfRowPairs: Int, showSpaceship: Boolean = true): String = accumulateRows(numOfRowPairs) + "\n" + emptySpaceshipRow

  private def getRandomRow: String = {
    val randomInt = Random().between(0, 3)
    spaceRowMap(randomInt)
  }

  @tailrec
  private def accumulateRows(maxRows: Int, currentDisplay: String = emptySpaceRow, rowAccumulator: Int = 0): String =
    if (rowAccumulator == maxRows) currentDisplay
    else {
      val newDisplay = s"$getRandomRow\n$currentDisplay"
      accumulateRows(maxRows, newDisplay, rowAccumulator + 1)
    }

}
