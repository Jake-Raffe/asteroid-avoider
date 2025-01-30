package games

import common.*
import configs.GameConstants
import javafx.scene.input.{KeyCode, KeyEvent}
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.*
import scalafx.scene.Scene
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.{Rectangle, Sphere}
import states.State

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

trait GameEngine[S <: State] extends JFXApp3 {

  val config: GameConstants
  val resetState: S
  val state: ObjectProperty[S]
  val frame: IntegerProperty            = IntegerProperty(0)
  val frameIncrementor: IntegerProperty = IntegerProperty(0)
  val scrollAccelerator: LongProperty   = LongProperty(0)

  def handleKeyPress(key: KeyEvent): Unit

  def onFrameChange(): Unit

  def runScene(): Scene

  private def gameLoop(): Unit =
    Future {
      frame.update(frame.value + frameIncrementor.value)
      Thread.sleep(config.scrollSpeed - scrollAccelerator.value)
    }.flatMap(_ => Future(gameLoop()))

  def resetGame(): Unit = {
    scrollAccelerator.update(0)
    frameIncrementor.update(0)
    frame.update(0)
    state.update(resetState)
  }

  override def start(): Unit = {

    frame.onChange {
      onFrameChange()
    }

    stage = new JFXApp3.PrimaryStage {
      title = config.gameTitle
      width = config.stageXBoundary
      height = config.stageYBoundary
      scene = runScene()
    }

    gameLoop()
  }

}
