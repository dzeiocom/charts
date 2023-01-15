// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.0")

        // Safe Navigation
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }
}

plugins {
    id("com.android.application") version "7.4.0" apply false
    id("com.android.library") version "7.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
}

task("clean") {
    delete(rootProject.buildDir)
    delete(project.buildDir)
}

