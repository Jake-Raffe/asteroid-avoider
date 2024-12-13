package asteroidAvoider

import asteroidAvoider.AsteroidAvoider.stage
import asteroidAvoider.ConfigGameConstants.*
import common.*
import common.ConfigGameConstants.objectWidth
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

object AsteroidAvoider extends JFXApp3 {

  override def start(): Unit = {
    val state: ObjectProperty[State]      = ObjectProperty(initialState)
    val frame: IntegerProperty            = IntegerProperty(0)
    val frameIncrementor: IntegerProperty = IntegerProperty(0)
    val scrollAccelerator: LongProperty   = LongProperty(0)

    // 3. as frame increases, vertical scroll will update the state
    frame.onChange {
      // Increase scroll rate over time
      if (frameScrollMap.contains(frame.value)) scrollAccelerator.update(frameScrollMap(frame.value))
      println(s"--- frame: ${frame.value} --- acceleration: ${scrollAccelerator.value}")
      if (state.value.gameInMotion) state.update(state.value.verticalScroll())
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Asteroid Avoider"
      width = sceneXBoundary
      height = sceneYBoundary
      scene = new Scene {
        fill = Black
        content = state.value.generateAllObjects
        onKeyPressed = handleKeyPress(_)
        // 4. when state is updated, regenerate game field
        state.onChange {
          if (state.value.collision) frameIncrementor.update(0)
          Platform.runLater {
            content = state.value.generateAllObjects
          }
        }
      }
    }

    // Scene: this is the game field that the game actually exists in
    // Stage/Window: this is the window that we see, W/H can be set and edited
    // Scene is always slightly larger than the stage on creation for some reason. Has to be edited in post:
    stage.setOnShown { _ =>
      stage.width = stage.scene().getWidth + objectWidth
      stage.height = stage.scene().getHeight + (3 * objectWidth)
    }

    // 1. start game loop
    // GameLoop can only ever be called once or multiple threads will run
    // To pause / play the game, the incrementer must be edited to that the thread is continuous but the frame is not always changing
    gameLoop()

    // 2. game loop thread will increment frame every 1s, triggering the .onChange method
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
      case KeyCode.SPACE if state.value.gameInMotion =>
        println(">>> Game PAUSED <<<")
        frameIncrementor.update(0)
        state.update(state.value.pauseGame())
      case KeyCode.SPACE =>
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
