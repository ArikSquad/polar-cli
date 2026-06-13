import org.gradle.api.tasks.compile.JavaCompile

plugins {
    application
    id("com.gradleup.shadow") version "9.4.1"
}

group = "eu.mikart"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.hollowcube:polar:1.16.0")
    implementation("net.minestom:minestom:2026.06.05-26.1.2")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.17")

    implementation("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

application {
    mainClass = "eu.mikart.polarcli.Main"
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
