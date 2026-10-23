pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // ✅ Add JitPack for PDF Viewer and other libraries
        maven { url = uri("https://jitpack.io") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        // ✅ Add JitPack again for runtime dependency resolution
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "iPrepare"
include(":app")
