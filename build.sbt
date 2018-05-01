name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.10.6"

libraryDependencies += javaJdbc
libraryDependencies += cache
libraryDependencies += javaWs
