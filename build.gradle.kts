import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm") version Versions.KOTLIN
    id("application")
    id("com.github.ben-manes.versions") version Versions.BEN_MANES_VERSIONS_PLUGIN
}

group = "org.jraf"
version = "1.0.0"

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    wrapper {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = Versions.GRADLE
    }


    // Configuration for gradle-versions-plugin
    withType<DependencyUpdatesTask> {
        resolutionStrategy {
            componentSelection {
                all {
                    if (setOf(
                            "alpha",
                            "beta",
                            "rc",
                            "preview",
                            "eap",
                            "m1"
                        ).any { candidate.version.contains(it, true) }
                    ) {
                        reject("Non stable")
                    }
                }
            }
        }
    }

    register("stage") {
        dependsOn(":installDist")
    }
}

application {
    mainClassName = "org.jraf.qontoapigraphqlbridge.main.MainKt"
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib-jdk8"))

    // Ktor
    implementation("io.ktor:ktor-server-core:${Versions.KTOR}")
    implementation("io.ktor:ktor-server-netty:${Versions.KTOR}")

    // Ktor GraphQL
    implementation("com.github.excitement-engineer:ktor-graphql:${Versions.KTOR_GRAPHQL}")

    // Logback
    runtimeOnly("ch.qos.logback:logback-classic:${Versions.LOGBACK}")
}
