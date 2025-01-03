package tetris

import asteroidAvoider.ConfigGameConstants.sceneXBoundary
import common.*
import common.ConfigGameConstants.objectWidth
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
import tetris.ConfigGameConstants.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

object Tetris extends JFXApp3 {

  // TODO
  // - Add score to the pause/end-game text (frame /100 ?)
  // - Increase speed over time
  // - Add option to see where shape would land if collides at current position and orientation?

  override def start(): Unit = {
    val state: ObjectProperty[State]      = ObjectProperty(initialState)
    val frame: IntegerProperty            = IntegerProperty(0)
    val frameIncrementor: IntegerProperty = IntegerProperty(0)
    val scrollAccelerator: LongProperty   = LongProperty(0)

    frame.onChange {
      if (frameScrollMap.contains(frame.value)) scrollAccelerator.update(frameScrollMap(frame.value))
      println(s"--- frame: ${frame.value} --- acceleration: ${scrollAccelerator.value}")
      if (state.value.gameState == GameInProgress) state.update(state.value.lowerShapeOnce())
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Tetris"
      width = stageXBoundary
      height = stageYBoundary
      scene = new Scene(sceneXBoundary, sceneYBoundary) {
        fill = Black
        content = state.value.generateAllObjects ++ List(state.value.displayText(), State.thresholdLine)
        onKeyPressed = handleKeyPress(_)
        state.onChange {
          state.update(state.value.checkIfOverThreshold())
          state.value.gameState match {
            case GameInProgress                       => frameIncrementor.update(1)
            case GameAtStart | GamePaused | Collision => frameIncrementor.update(0)
          }
          Platform.runLater {
            content = state.value.generateAllObjects ++ List(state.value.displayText(), State.thresholdLine)
          }
        }
      }
    }

    gameLoop()

    def gameLoop(): Unit =
      Future {
        frame.update(frame.value + frameIncrementor.value)
        Thread.sleep(scrollSpeed)
      }.flatMap(_ => Future(gameLoop()))

    def resetGame(): Unit = {
      frameIncrementor.update(0)
      frame.update(0)
      state.update(initialState)
    }

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
        state.update(state.value.dropShape())
      case KeyCode.R =>
        println("*** Game RESET ***")
        resetGame()
      case other => // Log for reference
        println(s"*** other key pressed: '$other'")
    }
  }

}
