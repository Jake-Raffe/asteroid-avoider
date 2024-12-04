import ConfigConstants.*
import javafx.scene.input.KeyCode
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.{Rectangle, Sphere}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

object Game extends JFXApp3 {

  override def start(): Unit = {
    val state: ObjectProperty[State] = ObjectProperty(initialState)
    val frame: IntegerProperty       = IntegerProperty(0)

    // 3. as frame increases, vertical scroll will update the state
    frame.onChange {
      println(s"--- frame: ${frame.value}")
      state.update(state.value.verticalScroll())
    }

    stage = new JFXApp3.PrimaryStage {
      width = sceneXBoundary
      height = sceneYBoundary
      scene = new Scene {
        fill = Black
        content = state.value.generateAllObjects
        // 3.5. if key pressed, move ship and update state
        onKeyPressed = key =>
          key.getCode match {
            case KeyCode.LEFT =>
              println("<<< Left <<<")
              state.update(state.value.moveShip(Left))
            case KeyCode.RIGHT =>
              println(">>> Right >>>")
              state.update(state.value.moveShip(Right))
            case KeyCode.R => // RESTART
              state.update(initialState)
            case other => // Log for reference
              println(s"*** other key pressed: '$other'")
          }

        // 4. when state is updated, regenerate game field
        state.onChange(Platform.runLater {
          content = state.value.generateAllObjects
        })
      }
    }

    // 1. start game loop
    gameLoop(() => frame.update(frame.value + 1)) // TODO add a press 'space' to start gameLoop rather than starting immediately
  }

  // 2. game loop thread will increment frame every 1s, triggering the .onChange method
  private def gameLoop(update: () => Unit): Unit =
    Future {
      update()
      Thread.sleep(scrollSpeed)
    }.flatMap(_ => Future(gameLoop(update)))

}
