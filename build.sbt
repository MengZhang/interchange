name := """interchange"""

version := "0.9.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)

doc in Compile <<= target.map(_ / "none")

mappings in Universal := {
  val orig = (mappings in Universal).value
  orig.filterNot { case (_, file) => file.endsWith("application.conf") || file.endsWith(".DS_Store")  }
}
