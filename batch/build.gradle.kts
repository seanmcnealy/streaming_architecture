plugins {
    id("java")
    application
}

group = "com.mcnealysoftware"
version = "1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation("org.postgresql:postgresql:42.7.7")

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    // junit-platform-launcher required https://github.com/gradle/gradle/issues/34512
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.mcnealysoftware.batch.BatchApplication")
}
