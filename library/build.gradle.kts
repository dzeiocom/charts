plugins {
    id("com.android.library")
    `maven-publish`
    kotlin("android")
}

val artifact = "charts"
group = "com.dzeio"
val projectVersion = project.findProperty("version") as String? ?: "0.1.0"
version = projectVersion

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = group as String?
            artifactId = artifact
            version = projectVersion

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

android {
    namespace = "$group.$artifact"
    compileSdk = 33
    buildToolsVersion = "33.0.0"

    defaultConfig {
        minSdk = 21
        targetSdk = 33
        aarMetadata {
            minCompileSdk = 21
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    testFixtures {
        enable = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}
