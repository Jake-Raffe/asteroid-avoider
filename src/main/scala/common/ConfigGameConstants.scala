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
}
