import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21"
    `maven-publish`
}

group = "uk.gov.dwp.dataworks"

repositories {
    mavenCentral()
    jcenter()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/dwp/dataworks-common-logging")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications    {
        register("gpr", MavenPublication::class) {
            from(components["java"])
        }
    }
}

configurations.all {
    exclude(group="org.slf4j", module="slf4j-log4j12")
}

dependencies {
    // Kotlin things
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // Logging things
    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    // Utility things
    implementation("org.apache.commons:commons-text:1.8")

    // JUnit things
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.0")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")

    // Testing helper things
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.assertj:assertj-core:3.15.0")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.10.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
