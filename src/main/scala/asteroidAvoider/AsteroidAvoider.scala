package asteroidAvoider

import asteroidAvoider.AsteroidAvoider.stage
import asteroidAvoider.AsteroidAvoiderConfig
import common.*
import javafx.scene.input.{KeyCode, KeyEvent}
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.*
import scalafx.scene.Scene
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.{Rectangle, Sphere}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

object AsteroidAvoider extends JFXApp3 with AsteroidAvoiderConfig {

  // TODO
  // - Add score to the pause/end-game text
  // - Use different shape or image for the ship

  override def start(): Unit = {
    val state: ObjectProperty[State]      = ObjectProperty(initialState)
    val frame: IntegerProperty            = IntegerProperty(0)
    val frameIncrementor: IntegerProperty = IntegerProperty(0)
    val scrollAccelerator: LongProperty   = LongProperty(0)

    frame.onChange {
      if (frameScrollMap.contains(frame.value)) scrollAccelerator.update(frameScrollMap(frame.value))
      println(s"--- frame: ${frame.value} --- acceleration: ${scrollAccelerator.value}")
      if (state.value.gameState == GameInProgress) state.update(state.value.verticalScroll())
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Asteroid Avoider"
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
        Thread.sleep(scrollSpeed - scrollAccelerator.value)
      }.flatMap(_ => Future(gameLoop()))

    def resetGame(): Unit = {
      scrollAccelerator.update(0)
      frameIncrementor.update(0)
      frame.update(0)
      state.update(initialState)
    }

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
  }

}
