lazy val Scala3 = "3.4.0"

Global / scalaVersion         := Scala3
Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / version := "0.0.1"

ThisBuild / testFrameworks += new TestFramework("munit.Framework")

ThisBuild / organization         := "xyz.didx"
ThisBuild / organizationName     := "DIDx"
ThisBuild / organizationHomepage := Some(url("https://www.didx.co.za/"))

ThisBuild / githubOwner      := "didx-xyz"
ThisBuild / githubRepository := "gleibnif"
githubTokenSource := TokenSource.GitConfig("github.token") || TokenSource.Environment(
  "GITHUB_TOKEN"
)

lazy val root = project
  .in(file("."))
  .aggregate(core, protocol, client, server)
  .settings(scalafixSettings)
  .settings(
    publish / skip       := true,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value
      .withOverwrite(true)
  )

lazy val bouncyCastleVersion = "1.77"
lazy val castanetVersion     = "0.1.11"
lazy val catsVersion         = "2.10.0"
lazy val ceVersion           = "3.5.4"
lazy val circeVersion        = "0.14.6"
lazy val didCommVersion      = "0.3.2"
lazy val emilVersion         = "0.17.0"
lazy val fs2Version          = "3.9.4"
lazy val googleProtoVersion  = "3.25.3"
lazy val grpcVersion         = "1.62.2"
lazy val http4sVersion       = "0.23.26"
lazy val ipfsVersion         = "1.4.4"
lazy val log4catsVersion     = "2.6.0"
lazy val logbackVersion      = "1.5.3"
lazy val munitVersion        = "1.0.0-M11"
lazy val munitCEVersion      = "2.0.0-M4"
lazy val openAIVersion       = "0.5.0"
lazy val passkitVersion      = "0.3.4"
lazy val pureconfigVersion   = "0.17.6"
lazy val redis4catsVersion   = "1.5.2"
lazy val scodecVersion       = "1.1.38"
lazy val shapelessVersion    = "3.4.1"
lazy val sttpVersion         = "3.9.3"
lazy val sttpApispecVersion  = "0.6.3"
lazy val tapirVersion        = "1.6.4"
lazy val tinkVersion         = "1.12.0"
lazy val titaniumVersion     = "1.4.0"
lazy val xebiaVersion        = "0.0.3"

lazy val commonSettings = Seq(
  resolvers ++= Seq(
    "github" at "https://maven.pkg.github.com/didx-xyz",
    Resolver.mavenLocal,
    "jitpack" at "https://jitpack.io",
    "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "releases" at "https://oss.sonatype.org/content/repositories/releases"
  ),
  libraryDependencies ++= Seq(
    "org.typelevel"                 %% "cats-core"               % catsVersion,
    "co.fs2"                        %% "fs2-core"                % fs2Version,
    "co.fs2"                        %% "fs2-io"                  % fs2Version,
    "org.typelevel"                 %% "cats-effect"             % ceVersion,
    "org.scodec"                    %% "scodec-bits"             % scodecVersion,
    "org.scala-lang"                %% "scala3-staging"          % Scala3,
    "io.circe"                      %% "circe-yaml"              % "0.14.2",
    "xyz.didx"                      %% "castanet"                % castanetVersion,
    "org.typelevel"                 %% "cats-core"               % catsVersion,
    "org.typelevel"                 %% "cats-effect"             % ceVersion,
    "org.bouncycastle"               % "bcpkix-jdk18on"          % bouncyCastleVersion,
    "org.typelevel"                 %% "log4cats-core"           % log4catsVersion,
    "org.typelevel"                 %% "log4cats-slf4j"          % log4catsVersion,
    "com.github.ipfs"                % "java-ipfs-http-client"   % ipfsVersion,
    "com.github.pureconfig"         %% "pureconfig-core"         % pureconfigVersion,
    "com.github.pureconfig"         %% "pureconfig-cats-effect"  % pureconfigVersion,
    "dev.profunktor"                %% "redis4cats-effects"      % redis4catsVersion,
    "dev.profunktor"                %% "redis4cats-log4cats"     % redis4catsVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-core"              % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server"     % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"        % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-sttp-stub-server"  % tapirVersion   % Test,
    "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-bundle" % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"      % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-asyncapi-docs"     % tapirVersion,
    "com.github.eikek"              %% "emil-common"             % emilVersion,
    "com.github.eikek"              %% "emil-javamail"           % emilVersion,
    "com.softwaremill.sttp.client3" %% "core"                    % sttpVersion,
    "com.softwaremill.sttp.apispec" %% "apispec-model"           % sttpApispecVersion,
    "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml"      % sttpApispecVersion,
    "org.http4s"                    %% "http4s-blaze-server"     % "0.23.16",
    "org.http4s"                    %% "http4s-dsl"              % http4sVersion,
    "ch.qos.logback"                 % "logback-classic"         % logbackVersion,
    "org.scalameta"                 %% "munit"                   % munitVersion   % Test,
    "org.scalameta"                 %% "munit-scalacheck"        % munitVersion   % Test,
    "org.typelevel"                 %% "munit-cats-effect"       % munitCEVersion % Test,
    "com.xebia"                     %% "xef-scala"               % xebiaVersion,
    "com.xebia"                      % "xef-pdf"                 % xebiaVersion   % "runtime",
    "com.xebia"                      % "xef-reasoning-jvm"       % xebiaVersion,
    "com.xebia" % "xef-openai" % xebiaVersion % "runtime" pomOnly ()
  ),
  libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)
)

lazy val grpcSettings = Seq(
  libraryDependencies ++= Seq(
    "io.grpc" % "grpc-netty-shaded",
    "io.grpc" % "grpc-core",
    "io.grpc" % "grpc-protobuf",
    "io.grpc" % "grpc-stub",
    "io.grpc" % "grpc-netty-shaded"
  ).map(_ % grpcVersion)
)

lazy val core = project
  .in(file("modules/core"))
  .settings(scalafixSettings)
  .settings(commonSettings: _*)
  .settings(
    name             := "gleibnifCore",
    crossPaths       := false,
    autoScalaLibrary := false,
    resolvers ++= Seq(
      Resolver.mavenLocal,
      "google" at "https://maven.google.com/"
    ),
    libraryDependencies ++= Seq(
      "org.didcommx"           % "didcomm"          % didCommVersion,
      "com.apicatalog"         % "titanium-json-ld" % titaniumVersion,
      "org.glassfish"          % "jakarta.json"     % "2.0.1",
      "com.google.crypto.tink" % "tink"             % tinkVersion,
      "com.google.crypto.tink" % "tink-awskms"      % "1.9.1"
    )
  )

lazy val protocol = project
  .in(file("modules/protocol"))
  .settings(scalafixSettings)
  .settings(
    name        := "gleibnifProtocol",
    description := "Protobuf definitions",
    /*  Compile / PB.targets := Seq(
      //PB.gens.java -> (Compile / sourceManaged).value,
      scalapb.gen() -> (Compile / sourceManaged).value,
      scalapb.gen(flatPackage = true) -> (Compile / sourceManaged).value
    ), */
    libraryDependencies ++= Seq(
      "com.google.protobuf" % "protobuf-java" % googleProtoVersion % "protobuf"
    )
  )
  .enablePlugins(Fs2Grpc)

lazy val client = project
  .in(file("modules/client"))
  .settings(scalafixSettings)
  .settings(
    name                := "gleibnifClient",
    description         := "Protobuf Client",
    Compile / mainClass := Some("xyz.didx.gleibnif.Main"),
    resolvers ++= Seq(
      Resolver.mavenLocal,
      "jitpack" at "https://jitpack.io",
      "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "releases" at "https://oss.sonatype.org/content/repositories/releases"
    ),
    libraryDependencies ++= Seq(
      "com.github.kenglxn.QRGen"       % "javase"                         % "3.0.1",
      "com.softwaremill.sttp.client3" %% "core"                           % sttpVersion,
      "com.softwaremill.sttp.client3" %% "circe"                          % sttpVersion,
      "com.softwaremill.sttp.client3" %% "cats"                           % sttpVersion,
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % sttpVersion,
      "com.apicatalog"                 % "titanium-json-ld"               % titaniumVersion,
      "org.glassfish"                  % "jakarta.json"                   % "2.0.1",
      "org.didcommx"                   % "didcomm"                        % didCommVersion,
      "io.cequence"                   %% "openai-scala-client"            % openAIVersion,
      "de.brendamour"                  % "jpasskit"                       % passkitVersion,
      "com.google.crypto.tink"         % "tink"                           % tinkVersion,
      "org.typelevel"                 %% "shapeless3-deriving"            % shapelessVersion
    ),
    scalapbCodeGeneratorOptions += CodeGeneratorOption.FlatPackage
  )
  .settings(commonSettings: _*)
  .settings(grpcSettings: _*)
  .enablePlugins(Fs2Grpc)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    dockerBaseImage      := "openjdk:21-jdk-slim",
    Docker / packageName := "dawnpatrol",
    Docker / version     := "latest"
  )
  .dependsOn(protocol)
  .dependsOn(core)
  .dependsOn(protocol % "protobuf")

lazy val server = project
  .in(file("modules/server"))
  .settings(scalafixSettings)
  .settings(commonSettings: _*)
  .settings(grpcSettings: _*)
  .settings(
    scalaVersion := Scala3,
    name         := "gleibnifServer",
    description  := "Protobuf Server",
    // nativeImageVersion := "21.2.0",
    Compile / mainClass := Some("xyz.didx.gleibnif.Main"),
    libraryDependencies ++= List(
      "xyz.didx"      %% "castanet"          % castanetVersion,
      "org.typelevel" %% "cats-core"         % catsVersion,
      "co.fs2"        %% "fs2-core"          % fs2Version,
      "co.fs2"        %% "fs2-io"            % fs2Version,
      "org.typelevel" %% "cats-effect"       % ceVersion,
      "io.grpc"        % "grpc-netty-shaded" % grpcVersion,
      "io.grpc"        % "grpc-core"         % grpcVersion,
      "io.grpc"        % "grpc-services"     % grpcVersion
    ),
    scalapbCodeGeneratorOptions += CodeGeneratorOption.FlatPackage
  )
  .enablePlugins(Fs2Grpc)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    dockerBaseImage      := "openjdk:21-jdk-slim",
    Docker / packageName := "dwn-grpc-server",
    Docker / version     := "latest"
  )
  .dependsOn(protocol)
  .dependsOn(protocol % "protobuf")

lazy val scalafixSettings = Seq(semanticdbEnabled := true)
