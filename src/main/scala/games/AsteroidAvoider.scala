package games

import common.*
import configs.{AsteroidAvoiderConfig, GameConstants}
import javafx.scene.input.{KeyCode, KeyEvent}
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.*
import scalafx.scene.Scene
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.{Rectangle, Sphere}
import states.AsteroidAvoiderState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

object AsteroidAvoider extends GameEngine[AsteroidAvoiderState] {

  val config: GameConstants = AsteroidAvoiderConfig

  val resetState: AsteroidAvoiderState            = AsteroidAvoiderConfig.initialState
  val state: ObjectProperty[AsteroidAvoiderState] = ObjectProperty(resetState)

  def handleKeyPress(key: KeyEvent): Unit = key.getCode match {
    case KeyCode.SPACE if state.value.gameState == GameInProgress =>
      println(">>> Game PAUSED <<<")
      frameIncrementor.update(0)
      state.update(state.value.pauseGame())
    case KeyCode.SPACE if state.value.gameState != Collision =>
      println("<<< Game STARTED >>>")
      frameIncrementor.update(1)
      state.update(state.value.startGame())
    case KeyCode.LEFT =>
      println("<<< Left <<<")
      state.update(state.value.moveShip(Left))
    case KeyCode.RIGHT =>
      println(">>> Right >>>")
      state.update(state.value.moveShip(Right))
    case KeyCode.R =>
      println("*** Game RESET ***")
      resetGame()
    case other => // Log for reference
      println(s"*** other key pressed: '$other'")
  }

  def onFrameChange(): Unit = {
    if (config.frameScrollMap.contains(frame.value)) scrollAccelerator.update(config.frameScrollMap(frame.value))
    println(s"--- frame: ${frame.value} --- acceleration: ${scrollAccelerator.value}")
    if (state.value.gameState == GameInProgress) state.update(state.value.verticalScroll())
  }

  def runScene(): Scene = new Scene(config.sceneXBoundary, config.sceneYBoundary) {
    fill = Black
    content = state.value.generateAllObjects.appended(state.value.displayText(frame.value))
    onKeyPressed = handleKeyPress(_)
    state.onChange {
      state.value.currentGameState match {
        case GameInProgress                       => frameIncrementor.update(1)
        case GameAtStart | GamePaused | Collision => frameIncrementor.update(0)
      }
      Platform.runLater {
        content = state.value.generateAllObjects.appended(state.value.displayText(frame.value))
      }
    }
  }

}
