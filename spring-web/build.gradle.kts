plugins {
    id("java")
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.mcnealysoftware"
version = "1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.flywaydb:flyway-database-postgresql:10.18.2")

    implementation(project(":common"))

    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")
}

tasks.test {
    useJUnitPlatform()
}
