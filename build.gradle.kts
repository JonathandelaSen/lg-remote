plugins {
    id("com.android.application") version "9.2.0" apply false
    // Debe coincidir con la versión de Kotlin que trae el plugin de Android (AGP 9.2 → 2.2.10).
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10" apply false
}
