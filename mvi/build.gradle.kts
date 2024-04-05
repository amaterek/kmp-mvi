plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("maven-publish")
}

kotlin {
    jvm()
    js { browser {} }
    @Suppress("OPT_IN_USAGE")
    wasmJs { browser {} }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "util.ui.mvi"
            isStatic = true
        }
    }

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = libs.versions.jvmTarget.get()
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.coroutines.test)
            implementation(libs.kotlin.test)
            implementation(libs.mockk.common)
            implementation(libs.turbine)
        }
        jvmTest.dependencies {
            implementation(libs.junit)
            implementation(libs.mockk)
        }
    }

    android {
        publishLibraryVariants("release")
    }
}

android {
    namespace = "amaterek.util.ui.mvi"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    dependencies {
        testImplementation(libs.junit)
        testImplementation(libs.mockk)
    }
}

publishing {
    repositories {
        maven {
            publications {
                register("release", MavenPublication::class) {
                    groupId = "com.github.amaterek"
                    artifactId = "kmp-mvi"
                    version = "0.1"
                }
            }
        }
    }
}