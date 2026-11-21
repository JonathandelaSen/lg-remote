# Normas del proyecto

- Siempre en `main`, sin ramas.
- Todo cambio de código lleva sus tests en el mismo commit (`./gradlew testDebugUnitTest`).
- Documentación y textos de la app en español.
- Antes de dar algo por bueno: compilar el release y revisarlo con capturas a 360 dp de ancho
  (`adb shell wm size 1080x2400 && adb shell wm density 480`, y luego `wm size reset` / `wm density reset`).
- Un código IR nuevo se añade a `LgKey` y, si hay valor publicado, a la tabla de `LgKeyTest`.
