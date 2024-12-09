package tetris

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
import tetris.ConfigGameConstants.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

object Tetris extends JFXApp3 {

  override def start(): Unit = {
    val state: ObjectProperty[State]      = ObjectProperty(initialState)
    val frame: IntegerProperty            = IntegerProperty(0)
    val frameIncrementor: IntegerProperty = IntegerProperty(0)

    frame.onChange {
      println(s"--- frame: ${frame.value}")
      if (state.value.gameInMotion) state.update(state.value.lowerShapeOnce())
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Tetris Rip-Off"
      width = sceneXBoundary
      height = sceneYBoundary
      scene = new Scene {
        fill = Black
        content = state.value.generateAllObjects
        onKeyPressed = handleKeyPress(_)
        state.onChange {
          if (state.value.collision) frameIncrementor.update(0)
          Platform.runLater {
            content = state.value.generateAllObjects
          }
        }
      }
    }

    stage.setOnShown { _ =>
      stage.width = stage.scene().getWidth + (2 * objectWidth)
      stage.height = stage.scene().getHeight + (4 * objectWidth)
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
        state.update(state.value.moveShape(Left))
      case KeyCode.RIGHT =>
        println(">>> Right >>>")
        state.update(state.value.moveShape(Right))
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
