ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "asteroid-avoider"
  )

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.16" % Test,
  "org.scalafx"   %% "scalafx"   % "16.0.0-R25"
)
lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
libraryDependencies ++= javaFXModules.map(m => "org.openjfx" % s"javafx-$m" % "16" classifier "mac")
// TODO configure versions to avoid: "WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @52a6406c'"
// libraryDependencies are configured for scala 2.13.8. App if build on version 3.2.2
