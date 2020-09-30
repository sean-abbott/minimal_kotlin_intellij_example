import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("com.google.cloud.tools.jib") version "2.5.0"
}

dependencies {
    implementation(project(":lib"))
}

application {
    mainClassName = "com.company.group.runner.JobRunnerKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

jib {
    val tag = System.getenv("BUILD_NUMBER") ?: System.getenv("USER")
    to {
        image = "group/kt-tasks:${tag}"
    }
}
