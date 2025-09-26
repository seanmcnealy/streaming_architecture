plugins {
    id("java")
    `java-library`
}

group = "com.mcnealysoftware"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.7")

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    // junit-platform-launcher required https://github.com/gradle/gradle/issues/34512
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.testcontainers:testcontainers:1.21.3")
    testImplementation("org.testcontainers:postgresql:1.21.3")
    testImplementation("com.zaxxer:HikariCP:7.0.2")
    // flyway-core required https://github.com/flyway/flyway/issues/4145
    testImplementation("org.flywaydb:flyway-core:11.13.1")
    testImplementation("org.flywaydb:flyway-database-postgresql:11.13.1")
}

tasks.test {
    useJUnitPlatform()
}