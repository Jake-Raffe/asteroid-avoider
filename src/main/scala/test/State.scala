package test

import common.GameState
import scalafx.scene.layout.VBox
import scalafx.scene.shape.Rectangle

trait State {

  val currentGameState: GameState

  def generateAllObjects: List[Rectangle]

  def displayText(score: Int): VBox

}
