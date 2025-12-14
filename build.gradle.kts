plugins {
    kotlin("jvm") version "2.2.20"
}

group = "name.currie.david"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("tools.aqua:z3-turnkey:4.14.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}