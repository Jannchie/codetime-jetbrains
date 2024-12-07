import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.20"
    id("org.jetbrains.intellij.platform") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.5"
    kotlin("plugin.serialization") version "2.0.20"
}

group = "dev.codetime"
version = "1.0.3"

repositories {
    mavenCentral()
    intellijPlatform {

        defaultRepositories()
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellijPlatform {
    pluginConfiguration {
        name = "Code Time"
    }
}

val ktorVersion: String by project
tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    prepareSandbox {
        dependsOn(shadowJar)
        from(shadowJar)
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
    shadowJar {
        archiveClassifier.set("")
    }
}

intellijPlatform {
    // ...

    pluginVerification {
        // ...

        ides {
            ide(IntelliJPlatformType.IntellijIdeaUltimate, "2024.3")
            local(file("/path/to/ide/"))
            recommended()
            select {
                types = listOf(IntelliJPlatformType.PhpStorm)
                channels = listOf(ProductRelease.Channel.RELEASE)
                sinceBuild = "232"
                untilBuild = "241.*"
            }
        }
    }
}
dependencies {
    intellijPlatform {
        intellijIdeaUltimate("2024.3")
        instrumentationTools()
        jetbrainsRuntime()
        pluginVerifier()

    }
    implementation("org.slf4j:slf4j-nop:2.0.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

}

