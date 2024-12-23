import common.ConfigGameConstants.objectWidth
import common.ObjectLocation
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpec
import tetris.ExistingBlocks

import java.io.FileNotFoundException

class TetrisSpec extends AnyWordSpec with TableDrivenPropertyChecks {

  val emptyList = List.empty[ObjectLocation]
  val singleBlockInMultipleRows: List[ObjectLocation] =
    List(ObjectLocation(0, 0), ObjectLocation(0, 300), ObjectLocation(0, 200), ObjectLocation(0, 500))

  def fullRow(yAxis: Double): List[ObjectLocation] =
    List.tabulate(14)(i => ObjectLocation(i * objectWidth, yAxis))

  def listInAscendingOrder(list: List[ObjectLocation]): List[ObjectLocation] = list.sortBy(+_.yAxis)

  private val completeRowCases = Table(
    ("description", "input"),
    ("list is empty", emptyList),
    ("input is a single full row", fullRow(0)),
    ("input is all full rows", fullRow(0) ++ fullRow(400) ++ fullRow(150))
  )

  private val incompleteRowCases = Table(
    ("input", "expectedResult"),
    (List(ObjectLocation(0, 400)), List(ObjectLocation(0, 400))),
    (fullRow(50) ++ List(ObjectLocation(0, 400)), List(ObjectLocation(0, 400))),
    (singleBlockInMultipleRows, singleBlockInMultipleRows),
    (fullRow(0) ++ List(ObjectLocation(0, 200), ObjectLocation(50, 300)) ++ fullRow(300), List(ObjectLocation(0, 225))),
    (
      List(ObjectLocation(0, 400)) ++ fullRow(350) ++ List(ObjectLocation(0, 100)) ++ fullRow(50) ++ List(ObjectLocation(0, 0)),
      List(ObjectLocation(0, 400), ObjectLocation(0, 125), ObjectLocation(0, 50)))
  )

  "Shape .removeRowIfFull" should {
    "return an empty list" when {
      completeRowCases.foreach { (description, input) =>
        description in {
          val existingBlocks = ExistingBlocks(input)

          assert(existingBlocks.removeRowIfFull().squares == emptyList)
        }
      }
    }

    "return list OLs not in a complete row, increasing the y axis by 'movementAmount' for each full row removed BELOW" in {
      incompleteRowCases.foreach { (input, expectedResult) =>
        val existingBlocks = ExistingBlocks(input)

        assert(existingBlocks.removeRowIfFull().squares == listInAscendingOrder(expectedResult))
      }
    }
  }
}
