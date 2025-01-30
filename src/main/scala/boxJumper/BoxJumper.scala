package boxJumper

import common.*
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

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

object BoxJumper extends JFXApp3 with BoxJumperConfig {

  // TODO
  // - Add random elements and variance to the obstacles
  // - Use different shapes
  // - Add a spinning motion to the jump animation

  override def start(): Unit = {
    val state: ObjectProperty[State]      = ObjectProperty(initialState)
    val frame: IntegerProperty            = IntegerProperty(0)
    val frameIncrementor: IntegerProperty = IntegerProperty(0)
    val scrollAccelerator: LongProperty   = LongProperty(0)

    frame.onChange {
      if (frameScrollMap.contains(frame.value)) scrollAccelerator.update(frameScrollMap(frame.value))
      if (state.value.gameState == GameInProgress) state.update(state.value.horizontalScroll())
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Box Jumper"
      width = stageXBoundary
      height = stageYBoundary
      scene = new Scene(sceneXBoundary, sceneYBoundary) {
        fill = Black
        content = state.value.generateAllObjects.appended(state.value.displayText(frame.value))
        onKeyPressed = handleKeyPress(_)
        state.onChange {
          state.value.gameState match {
            case GameInProgress                       => frameIncrementor.update(1)
            case GameAtStart | GamePaused | Collision => frameIncrementor.update(0)
          }
          Platform.runLater {
            content = state.value.generateAllObjects.appended(state.value.displayText(frame.value))
          }
        }
      }
    }

    gameLoop()

    def gameLoop(): Unit =
      Future {
        frame.update(frame.value + frameIncrementor.value)
        Thread.sleep(runSpeed - scrollAccelerator.value)
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
      case KeyCode.UP if state.value.jumpFrame == 0 =>
        println("""/// JUMP \\\""")
        state.update(state.value.jump())
      case KeyCode.R =>
        println("*** Game RESET ***")
        resetGame()
      case other => // Log for reference
        println(s"*** other key pressed: '$other'")
    }
  }

}
