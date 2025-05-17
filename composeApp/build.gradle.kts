import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)

    id("app.cash.sqldelight") version "2.0.1"
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.android.driver)

        }
        commonMain.dependencies {
            // Basic dependencies
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Retrofit for easier http connection
            implementation(libs.retrofit)
            implementation(libs.converter.jackson) // Para usar Jackson
            implementation(libs.kotlinx.coroutines.core)

            // Reflexion
            implementation(libs.kotlin.reflect)

            // Horizontal carousel
            implementation(libs.accompanist.pager)

            // Horizontal list of models
            implementation(libs.accompanist.flowlayout)

            // Navigation
            implementation(libs.navigation.compose)

            runtimeOnly(libs.lifecycle.runtime)
            runtimeOnly(libs.androidx.lifecycle.lifecycle.viewmodel)
            runtimeOnly(libs.androidx.lifecycle.livedata)

            // Serialization
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.retrofit2.kotlinx.serialization.converter)

            // For email validation
            implementation(libs.commons.validator)

            // Logging
            implementation(libs.kotlin.logging.jvm)
            implementation(libs.logback.classic)
            implementation(libs.androidx.material3)
            implementation(libs.androidx.core)
            implementation(libs.androidx.core.ktx)

            // HTTP
            implementation(project.dependencies.platform(libs.http4k.bom))
            implementation(libs.http4k.core)
            implementation(libs.http4k.server.undertow)
            implementation(libs.http4k.client.apache)


            // Datastore
            api(libs.datastore.preferences)
            api(libs.datastore)

            implementation(libs.coroutines.extensions)

            implementation(libs.okhttp)

            // Toaster
            implementation(libs.sonner)

            // File picker
            implementation(libs.filekit.dialogs)

            // Full list of icons
            implementation(libs.androidx.material.icons.extended)

            // File zipper
            implementation("net.lingala.zip4j:zip4j:2.11.5")

            //
            //implementation(libs.sceneview)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            // SqlDelight
            implementation(libs.sqlite.driver)
        }
    }
}

android {
    namespace = "org.example.project"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.example.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"  // Exclude META-INF/INDEX.LIST
            excludes += "/META-INF/DEPENDENCIES"  // Exclude META-INF/DEPENDENCIES
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildToolsVersion = "35.0.0"
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.animation.android)
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.example.project.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}

sqldelight {
    databases {
        create("PrintStainDatabase") {
            packageName.set("org.example.project")
            version = 5
            generateAsync.set(true)
        }
    }
}
