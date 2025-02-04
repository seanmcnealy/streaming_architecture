plugins {
    id("java")
    application
}

group = "com.mcnealysoftware"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation("org.postgresql:postgresql:42.7.3")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.mcnealysoftware.batch.BatchApplication")
}
