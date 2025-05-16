rootProject.name = "PrintStain"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        
        // Repositorio de JitPack para bibliotecas de GitHub
        maven("https://jitpack.io")
        
        // Repositorio de Sonatype para snapshots
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        
        // Repositorio local de Maven (opcional)
        mavenLocal()
    }
}

include(":composeApp")