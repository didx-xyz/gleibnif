resolvers += Resolver.mavenLocal

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.5.4")

addSbtPlugin("org.scalameta" % "sbt-native-image" % "0.3.4")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.0")

addSbtPlugin("org.typelevel" % "sbt-fs2-grpc" % "2.7.16")

addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.10.0")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.12.1")

// addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta42")

addSbtPlugin("com.codecommit" % "sbt-github-packages" % "0.5.3")
