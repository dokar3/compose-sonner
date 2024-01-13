import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "compose-sooner"
        browser {
            commonWebpackConfig {
                outputFileName = "compose-sooner.js"
            }

            testTask {
                // Tests are broken now: Module not found: Error: Can't resolve './skiko.mjs'
                enabled = false
            }
        }
        binaries.executable()
    }

    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.coroutines.core)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        val desktopTest by getting {
            dependencies {
                implementation(compose.desktop.uiTestJUnit4)
            }
        }
    }
}

android {
    namespace = "com.dokar.sonner.core"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.experimental {
    web.application {}
}
