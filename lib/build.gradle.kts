import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test> {
    this.environment["STAGE"] = "test"
}

dependencies {
    implementation("com.sksamuel.hoplite", "hoplite-core", "1.3.3")
    implementation("com.sksamuel.hoplite", "hoplite-yaml", "1.3.3")
    implementation("com.sksamuel.hoplite", "hoplite-aws", "1.3.1")

    implementation("net.snowflake", "snowflake-jdbc", "3.12.10")
    implementation("mysql", "mysql-connector-java", "8.0.21")

    // For xxhash
    implementation("net.jpountz.lz4", "lz4", "1.2.0")
}
