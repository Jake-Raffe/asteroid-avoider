package games

import common.*
import configs.{GameConstants, TetrisConfig}
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
import states.{State, TetrisState}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

object Tetris extends GameEngine[TetrisState] {

  // TODO
  // - Increase speed over time
  // - Add option to see where shape would land if collides at current position and orientation?
  // - Increase speed

  val config: GameConstants = TetrisConfig

  val resetState: TetrisState            = TetrisConfig.initialState
  val state: ObjectProperty[TetrisState] = ObjectProperty(resetState)

  def handleKeyPress(key: KeyEvent): Unit = key.getCode match {
    case KeyCode.SPACE if state.value.gameState == GameInProgress =>
      println(">>> Game PAUSED <<<")
      state.update(state.value.pauseGame())
    case KeyCode.SPACE =>
      println("<<< Game STARTED >>>")
      state.update(state.value.startGame())
    case KeyCode.LEFT =>
      println("<<< Left <<<")
      state.update(state.value.moveShape(Left))
    case KeyCode.RIGHT =>
      println(">>> Right >>>")
      state.update(state.value.moveShape(Right))
    case KeyCode.W =>
      println(">>> ClockWise >>>")
      state.update(state.value.rotateShape(ClockwiseRotate))
    case KeyCode.Q =>
      println("<<< AntiClockwise <<<")
      state.update(state.value.rotateShape(AntiClockwiseRotate))
    case KeyCode.DOWN =>
      println("""\\\ Down ///""")
      frame.update(frame.value + 5)
      state.update(state.value.dropShape())
    case KeyCode.R =>
      println("*** Game RESET ***")
      resetGame()
    case other => // Log for reference
      println(s"*** other key pressed: '$other'")
  }

  def onFrameChange(): Unit = {
    if (config.frameScrollMap.contains(frame.value)) scrollAccelerator.update(config.frameScrollMap(frame.value))
    println(s"--- frame: ${frame.value} --- acceleration: ${scrollAccelerator.value}")
    if (state.value.gameState == GameInProgress) state.update(state.value.lowerShapeOnce())
  }

  def runScene(): Scene = new Scene(config.sceneXBoundary, config.sceneYBoundary) {
    fill = Black
    content = state.value.generateAllObjects ++ List(state.value.displayText(frame.value), TetrisState.thresholdLine)
    onKeyPressed = handleKeyPress(_)
    state.onChange {
      state.update(state.value.checkIfOverThreshold())
      state.value.gameState match {
        case GameInProgress                       => frameIncrementor.update(1)
        case GameAtStart | GamePaused | Collision => frameIncrementor.update(0)
      }
      Platform.runLater {
        content = state.value.generateAllObjects ++ List(state.value.displayText(frame.value), TetrisState.thresholdLine)
      }
    }
  }

}
