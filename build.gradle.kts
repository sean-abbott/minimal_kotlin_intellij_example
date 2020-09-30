import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import com.bmuschko.gradle.docker.tasks.AbstractDockerRemoteApiTask
import com.bmuschko.gradle.docker.tasks.image.DockerPullImage
import org.gradle.api.Action

import java.time.Instant

allprojects {
    repositories {
        jcenter()
    mavenCentral()
    gradlePluginPortal()
    }
    group = "com.company.group"
    apply(plugin = "com.palantir.git-version")
    val gitVersion: groovy.lang.Closure<*> by extra
    version = gitVersion()

}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.unbroken-dome.test-sets")
    val gitVersion: groovy.lang.Closure<*> by extra
    version = gitVersion()

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.0")
        // logging
        implementation("io.github.microutils:kotlin-logging:1.8.3")
        implementation("org.slf4j", "slf4j-api", "1.7.30")
        implementation("org.apache.logging.log4j", "log4j-api", "2.13.3")
        implementation("org.apache.logging.log4j", "log4j-core", "2.13.3")
        implementation("org.apache.logging.log4j", "log4j-slf4j-impl", "2.13.3")
        implementation("com.fasterxml.jackson.core", "jackson-databind", "2.11.2")

        // orm
        implementation("me.liuwj.ktorm:ktorm-core:3.0.0")
        implementation("me.liuwj.ktorm:ktorm-support-mysql:3.0.0")
        implementation("me.liuwj.ktorm:ktorm-jackson:3.0.0")

        implementation("io.sentry:sentry:1.7.30")
        implementation("io.sentry:sentry-log4j2:1.7.30")

        // dependencies for Spek
        testImplementation(kotlin("test"))
        testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.7.0-RC1")
        testImplementation("org.spekframework.spek2", "spek-dsl-jvm", "2.0.12")
        testRuntimeOnly("org.spekframework.spek2", "spek-runner-junit5", "2.0.12")

        testImplementation("org.junit.platform", "junit-platform-engine", "1.3.0-RC1")

        testImplementation("io.mockk", "mockk", "1.10.0")

        // testcontainters for integration tests
        implementation(platform("org.testcontainers:testcontainers-bom:1.14.3"))
        testImplementation("org.testcontainers:mysql")
    }

    tasks.withType<Test> {
        useJUnitPlatform {
            includeEngines("spek2")
        }

        testLogging {
            lifecycle {
                events = mutableSetOf(TestLogEvent.FAILED,
                        TestLogEvent.SKIPPED,
                        TestLogEvent.STANDARD_OUT,
                        TestLogEvent.STANDARD_ERROR)

                exceptionFormat = TestExceptionFormat.FULL
                showExceptions = true
                showCauses = true
                showStackTraces = true
                showStandardStreams = true
            }
            info.events = lifecycle.events
            info.exceptionFormat = lifecycle.exceptionFormat
        }

        // try to get all failures
        ignoreFailures = true

        val testTask = this
        addTestListener(object : TestListener {
            override fun beforeSuite(suite: TestDescriptor) {}
            override fun beforeTest(testDescriptor: TestDescriptor) {}
            override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}

            override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                if (suite.parent != null) return // only for root project
                logger.lifecycle("----")
                logger.lifecycle("Test result: ${result.resultType}")
                logger.lifecycle("Test summary: ${result.testCount} tests, " +
                        "${result.successfulTestCount} succeeded, " +
                        "${result.failedTestCount} failed, " +
                        "${result.skippedTestCount} skipped")
                logger.lifecycle("Report file: ${testTask.reports.html.entryPoint}")
            }
        })
    }

    testSets {
        val integrationTest by creating {}
    }

    tasks.withType<Jar> {
        manifest {
            attributes(
                "Specification-Title" to "group-kt:${project.name}",
                "Specification-Version" to "1.0",
                "Specification-Vendor" to "Company",
                "Implementation-Title" to "group-kt:${project.name}",
                "Implementation-Version" to archiveVersion,
                "Implementation-Vendor" to "Company",
                "Implementation-Timestamp" to Instant.now()
            )
        }
        archiveFileName.set("${project.name}.jar")
    }
}

plugins {
    kotlin("jvm") version "1.4.0"
    id("com.palantir.git-version") version "0.12.3"
    id("com.github.ben-manes.versions") version "0.29.0"
    id("org.unbroken-dome.test-sets") version "3.0.1"
    id("com.bmuschko.docker-remote-api") version "6.6.1"
    id("com.dorongold.task-tree") version "1.5"
}


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
