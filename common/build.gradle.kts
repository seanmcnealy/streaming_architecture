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
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.testcontainers:testcontainers:1.21.3")
    testImplementation("org.testcontainers:postgresql:1.21.3")
    testImplementation("com.zaxxer:HikariCP:7.0.2")
    testImplementation("org.flywaydb:flyway-database-postgresql:11.13.1")
}

tasks.test {
    useJUnitPlatform()
}