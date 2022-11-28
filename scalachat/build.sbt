name := "MinimalApplication"

version := "0.3.0"

scalaVersion := "2.13.3"

fork := true
javaOptions ++= Seq("--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED")

//enablePlugins(ScalaJSPlugin)
//libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.1.0"
//scalaJSUseMainModuleInitializer := true;


// Run dependencies
libraryDependencies += "com.lihaoyi" % "cask_2.13" % "0.6.2"
libraryDependencies += "com.lihaoyi" % "scalatags_2.13" % "0.9.2"

// Test dependencies
libraryDependencies += "com.lihaoyi" % "utest_2.13" % "0.7.5" % "test"
libraryDependencies += "com.lihaoyi" % "requests_2.13" % "0.6.5" % "test"
