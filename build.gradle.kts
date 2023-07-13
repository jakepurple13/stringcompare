import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.programmersbox"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.material3)
                implementation(compose.desktop.components.splitPane)
                implementation("org.bitbucket.cowwoc:diff-match-patch:1.2")
                implementation("com.mikepenz:multiplatform-markdown-renderer-jvm:0.6.1")
                implementation("io.github.furstenheim:copy_down:1.1")
                implementation("io.github.java-diff-utils:java-diff-utils:4.12")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "stringcompare"
            packageVersion = "1.0.0"
        }
    }
}
