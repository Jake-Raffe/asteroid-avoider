package games

import common.*
import configs.{BoxJumperConfig, GameConstants}
import javafx.scene.input.{KeyCode, KeyEvent}
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.*
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.*
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.{Rectangle, Sphere}
import states.BoxJumperState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

object BoxJumper extends GameEngine[BoxJumperState] {

  // TODO
  // - Add random elements and variance to the obstacles
  // - Use different shapes
  // - Add a spinning motion to the jump animation

  val config: GameConstants = BoxJumperConfig

  val resetState: BoxJumperState            = BoxJumperConfig.initialState
  val state: ObjectProperty[BoxJumperState] = ObjectProperty(resetState)

  def handleKeyPress(key: KeyEvent): Unit = key.getCode match {
    case KeyCode.SPACE if state.value.gameState == GameInProgress =>
      println(">>> Game PAUSED <<<")
      state.update(state.value.pauseGame())
    case KeyCode.SPACE =>
      println("<<< Game STARTED >>>")
      state.update(state.value.startGame())
    case KeyCode.UP if state.value.jumpFrame == 0 =>
      println("""/// JUMP \\\""")
      state.update(state.value.jump())
    case KeyCode.R =>
      println("*** Game RESET ***")
      resetGame()
    case other => // Log for reference
      println(s"*** other key pressed: '$other'")
  }

  def onFrameChange(): Unit = {
    if (config.frameScrollMap.contains(frame.value)) scrollAccelerator.update(config.frameScrollMap(frame.value))
    println(s"--- frame: ${frame.value} --- acceleration: ${scrollAccelerator.value}")
    if (state.value.gameState == GameInProgress) state.update(state.value.horizontalScroll())
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
