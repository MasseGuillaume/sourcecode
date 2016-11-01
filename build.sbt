val baseSettings = Seq(
  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.0"),
  version := "0.2.0-scastie",
  name := "sourcecode",
  organization := "com.lihaoyi",
  publishTo := Some("releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"),
  scmInfo := Some(ScmInfo(
    browseUrl = url("https://github.com/lihaoyi/sourcecode"),
    connection = "scm:git:git@github.com:lihaoyi/sourcecode.git"
  )),
  licenses := Seq("MIT" -> url("http://www.opensource.org/licenses/mit-license.html")),
  developers += Developer(
    email = "haoyi.sg@gmail.com",
    id = "lihaoyi",
    name = "Li Haoyi",
    url = url("https://github.com/lihaoyi")
  )
)

baseSettings

def macroDependencies(version: String, binaryVersion: String) = {
  val quasiquotes = 
    if(binaryVersion == "2.10")
      Seq(
        compilerPlugin("org.scalamacros" % s"paradise" % "2.1.0" cross CrossVersion.full),
        "org.scalamacros" %% s"quasiquotes" % "2.1.0"
      )
    else Seq()

  Seq(
    "org.scala-lang" % "scala-reflect" % version % "provided",
    "org.scala-lang" % "scala-compiler" % version % "provided"
  ) ++ quasiquotes
}

lazy val sourcecode = crossProject2
  .settings(baseSettings)
  .settings(
    libraryDependencies ++= macroDependencies(scalaVersion.value, scalaBinaryVersion.value),
    unmanagedSourceDirectories in Compile ++= {
      if (Set("2.11", "2.12").contains(scalaBinaryVersion.value)) 
        Seq(baseDirectory.value / ".." / "shared" / "src" / "main" / "scala-2.11_2.12")
      else
        Seq()
    }
  )
  .nativeSettings(
    name := "sourcecode-native",
    nativeClangOptions := Stream(
      "-I/nix/store/rxvzdlp5x3r60b02fk95v404y3mhs2in-boehm-gc-7.2f-dev/include",
      "-L/nix/store/bw1p8rairfwv2yif2g1cc0yg8hv25mnl-boehm-gc-7.2f/lib"
    )
  )

lazy val js = sourcecode.js
lazy val jvm = sourcecode.jvm
lazy val native = sourcecode.native
