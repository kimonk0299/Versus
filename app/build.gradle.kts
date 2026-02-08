import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.versus.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.versus.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // ── TMDb API Key ──
        // Reads the key from local.properties so it never gets committed to git.
        // See README.md for setup instructions.
        val localPropertiesFile = rootProject.file("local.properties")
        val localProperties = Properties()
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        buildConfigField(
            "String",
            "TMDB_API_KEY",
            "\"${localProperties.getProperty("TMDB_API_KEY", "")}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true       // Allows us to use BuildConfig.TMDB_API_KEY
    }

    composeOptions {
        // Must match the Kotlin version (1.9.22 → Compose Compiler 1.5.10)
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ── Compose BOM (Bill of Materials) ──
    // Controls versions for ALL Compose libraries so they stay compatible.
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)

    // ── Jetpack Compose UI ──
    implementation("androidx.compose.ui:ui")                    // Core Compose UI
    implementation("androidx.compose.ui:ui-graphics")           // Graphics utilities
    implementation("androidx.compose.ui:ui-tooling-preview")    // Preview support
    implementation("androidx.compose.ui:ui-text-google-fonts")   // Google Fonts (Nunito, Fredoka)
    implementation("androidx.compose.material3:material3")      // Material 3 components
    implementation("androidx.compose.material:material-icons-extended") // Extra icons
    implementation("androidx.activity:activity-compose:1.8.2")  // Compose in Activity

    // ── Navigation ──
    // Handles moving between screens (Home → Bracket → Winner, etc.)
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ── ViewModel + Lifecycle ──
    // MVVM architecture: ViewModels survive screen rotation
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // ── Retrofit (Networking) ──
    // Makes HTTP calls to TMDb API to fetch actor/movie data
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ── Coil (Image Loading) ──
    // Loads movie poster images from URLs into Compose
    implementation("io.coil-kt:coil-compose:2.6.0")

    // ── Gson (JSON Parsing) ──
    // Parses JSON responses from TMDb API and the preset actors file
    implementation("com.google.code.gson:gson:2.10.1")

    // ── Coroutines ──
    // For async operations (API calls run in background, UI stays responsive)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // ── Core AndroidX ──
    implementation("androidx.core:core-ktx:1.12.0")

    // ── Debug tools (only included in debug builds) ──
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
