# Normas del proyecto

- Siempre en `main`, sin ramas.
- Todo cambio de código lleva sus tests en el mismo commit (`./gradlew testDebugUnitTest`).
- Documentación en español (el README tiene además un resumen en inglés).
- Textos de la app: inglés en `values/strings.xml` y cada idioma en `values-xx/strings.xml` (12 en total).
  `StringsTest` falla si falta una traducción, si sobra una clave o si hay un apóstrofo sin escapar.
  Los nombres de las teclas del mando (INPUT, Q.MENU, LIST…) no se traducen: son los del mando físico.
- Para añadir un idioma: crear `values-xx/strings.xml`, añadirlo a `localeFilters` en
  `app/build.gradle.kts`, a la lista de `StringsTest` y a la de idiomas del README.
- El mando se dibuja siempre de izquierda a derecha, también en árabe (ver `RemoteScreen`).
- Antes de dar algo por bueno: compilar el release y revisarlo con capturas a 360 dp de ancho
  (`adb shell wm size 1080x2400 && adb shell wm density 480`, y luego `wm size reset` / `wm density reset`).
- Un código IR nuevo se añade a `LgKey`, a la tabla de botones del README y, si hay valor publicado,
  a la tabla de `LgKeyTest`.

## Firma

`release.jks` y `keystore.properties` están en la raíz y **fuera de git**. Sin ellos no se pueden
publicar actualizaciones que se instalen encima de las anteriores: no borrarlos ni moverlos.

## Capturas del README (`docs/`)

Con el emulador arrancado, instalar el APK de **debug** y abrirlo con
`adb shell am start -n io.github.jonathandelasen.remoto/.MainActivity --ez screenshot true`
(oculta el aviso de «sin infrarrojos», que en el emulador siempre sale). El idioma se cambia con
`adb shell cmd locale set-app-locales io.github.jonathandelasen.remoto --locales es-ES`.

## Publicar una versión

1. Subir `versionCode` y `versionName` en `app/build.gradle.kts`.
2. `./gradlew testDebugUnitTest assembleRelease`
3. Copiar `app/build/outputs/apk/release/app-release.apk` como `Remoto.apk` (el README enlaza a
   `releases/latest/download/Remoto.apk`, así que el nombre no puede cambiar).
4. `gh release create vX.Y Remoto.apk --title "Remoto X.Y" --notes "…"`
