plugins {
    id("java")
    `java-library`
}

group = "com.mcnealysoftware"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.3")

    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.testcontainers:testcontainers:1.20.1")
    testImplementation("org.testcontainers:postgresql:1.20.1")
    testImplementation("com.zaxxer:HikariCP:5.1.0")
    testImplementation("org.flywaydb:flyway-database-postgresql:10.18.2")
}

tasks.test {
    useJUnitPlatform()
}