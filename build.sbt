name := "SparkSkeletonSbt"

version := "0.1"

scalaVersion := "2.11.12"

val sparkVersion = "2.3.1"
val domain = "jhole89"

addCommandAlias("sanity", ";clean ;compile ;assembly ;test ;coverage ;coverageReport ;scalastyle")

resolvers in Global ++= Seq(
  "apache-snapshots" at "http://repository.apache.org/snapshots/",
  "jitpack" at "https://jitpack.io",
  Resolver.jcenterRepo,
  "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

libraryDependencies ++= applicationDependencies ++ testDependencies

val applicationDependencies = Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.scala-sbt" %% "librarymanagement-ivy" % "1.2.2",
  "org.apache.hadoop" % "hadoop-aws" % "2.7.3" % Provided,
  "com.amazonaws" % "aws-java-sdk" % "1.7.4"
)

val testDependencies = Seq(
  "org.pegdown" % "pegdown" % "1.6.0" % "test",
  "org.scalatest" % "scalatest_2.11" % "3.0.5" % "test",
  "org.scoverage" %% "scalac-scoverage-runtime" % "1.0.4" % "test"
)

testOptions in Test ++= Seq(
  Tests.Argument(TestFrameworks.ScalaTest, "-o"),
  Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports")
)

parallelExecution in Test := false
fork in Test := true
javaOptions += "-Xmx2G"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

assemblyJarName in assembly := s"${name.value}-assembly.jar"

artifact in (Compile, assembly) := {
  val art = (artifact in (Compile, assembly)).value
  art.copy(`classifier` = Some("assembly"))
}

addArtifact(artifact in (Compile, assembly), assembly)


publishMavenStyle := false

lazy val root = (project in file(".")).settings(
  s3overwrite := true
)

publishTo := {
  val prefix = if (isSnapshot.value) "snapshots" else "releases"
  Some(
    s3resolver
      .value(s"$prefix S3 bucket", s3(s"mvn.$domain.com/$prefix"))
      .withIvyPatterns
  )
}
