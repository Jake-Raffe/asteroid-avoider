package common

import scalafx.scene.paint.Color.{Black, White}
import scalafx.scene.text.{Font, Text}

object ConfigGameConstants {

  val objectWidth: Double    = 25
  val movementAmount: Double = objectWidth

  val startGameText: Text = new Text("Press SPACE to Start!") {
    fill = Black
    stroke = White
    font = Font("Ariel", 30)
  }
  val pausedText: Text = new Text("Game Paused") {
    fill = Black
    stroke = White
    font = Font("Ariel", 30)
  }
  val gameOverText: Text = new Text("   GAME OVER!\nPress R to restart") {
    fill = Black
    stroke = White
    font = Font("Ariel", 30)
  }

  def createTextWithScore(gameState: GameState, score: Int) = {
    val content = gameState match {
      case GameAtStart => "Press SPACE to Start!"
      case Collision   => s"   GAME OVER!\n        Score: $score\nPress R to restart"
      case _           => s"Game Paused\n      Score: $score"
    }
    new Text(content) {
      fill = Black
      stroke = White
      font = Font("Ariel", 30)
    }
  }
}
