import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Clave de firma de las versiones publicadas: keystore.properties + release.jks en la raíz,
// los dos fuera de git. Sin ellos (p. ej. al clonar el repo) se firma con la clave de depuración.
val keystoreProps = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) file.inputStream().use(::load)
}

android {
    namespace = "io.github.jonathandelasen.remoto"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.jonathandelasen.remoto"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "1.1"
    }

    signingConfigs {
        if (!keystoreProps.isEmpty) {
            create("release") {
                storeFile = rootProject.file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    androidResources {
        // Permite elegir el idioma de la app en los ajustes de Android 13+.
        generateLocaleConfig = true
        // Solo los idiomas traducidos (las librerías traen ~80 y saldrían en ese selector).
        // Debe coincidir con las carpetas values-xx: lo comprueba StringsTest.
        localeFilters += listOf("en", "es", "fr", "de", "pt", "it", "ru", "ja", "zh-rCN", "hi", "ar", "in")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.12.00"))
    implementation("androidx.activity:activity-compose:1.12.4")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")

    testImplementation("junit:junit:4.13.2")
}
