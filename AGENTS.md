# Normas del proyecto

- Siempre en `main`, sin ramas.
- Todo cambio de código lleva sus tests en el mismo commit (`./gradlew testDebugUnitTest`).
- Documentación en español, salvo el README: `README.md` en inglés (el que ve la gente en GitHub) y
  `README.es.md` en español. Tienen el mismo contenido: todo cambio va en los dos.
- Textos de la app: inglés en `values/strings.xml` y cada idioma en `values-xx/strings.xml` (12 en total).
  `StringsTest` falla si falta una traducción, si sobra una clave o si hay un apóstrofo sin escapar.
  Los nombres de las teclas del mando (INPUT, Q.MENU, LIST…) no se traducen: son los del mando físico.
- Para añadir un idioma: crear `values-xx/strings.xml`, añadirlo a `localeFilters` en
  `app/build.gradle.kts`, a la lista de `StringsTest` y a la de idiomas de los dos README.
- El mando se dibuja siempre de izquierda a derecha, también en árabe (ver `RemoteScreen`).
- Antes de dar algo por bueno: compilar el release y revisarlo con capturas a 360 dp de ancho
  (`adb shell wm size 1080x2400 && adb shell wm density 480`, y luego `wm size reset` / `wm density reset`).
- Un código IR nuevo se añade a `LgKey`, a la tabla de botones de los dos README y, si hay valor publicado,
  a la tabla de `LgKeyTest`.

## Firma

`release.jks` y `keystore.properties` están en la raíz y **fuera de git**. Sin ellos no se pueden
publicar actualizaciones que se instalen encima de las anteriores: no borrarlos ni moverlos.

Por lo mismo, el `applicationId` (y el paquete) siguen siendo `io.github.jonathandelasen.remoto`, de
cuando la app se llamaba Remoto: no cambiarlos, o la versión nueva no se instalaría encima.

## Capturas del README (`docs/`)

Con el emulador arrancado, instalar el APK de **debug** y abrirlo con
`adb shell am start -n io.github.jonathandelasen.remoto/.MainActivity --ez screenshot true`
(oculta el aviso de «sin infrarrojos», que en el emulador siempre sale). El idioma se cambia con
`adb shell cmd locale set-app-locales io.github.jonathandelasen.remoto --locales es-ES`.
Hay dos por idioma (`screenshot-en.png` / `screenshot-en-more.png` para `README.md`, y las `-es` para
`README.es.md`); la segunda se saca tras `adb shell input swipe 640 2400 640 500 400`. Todas se reducen
a 720 px de ancho con `sips --resampleWidth 720`.

## Publicar una versión

1. Subir `versionCode` y `versionName` en `app/build.gradle.kts`.
2. `./gradlew testDebugUnitTest assembleRelease`
3. Copiar `app/build/outputs/apk/release/app-release.apk` como `LG-Remote.apk` (el README enlaza a
   `releases/latest/download/LG-Remote.apk`, así que el nombre no puede cambiar).
4. `gh release create vX.Y LG-Remote.apk --title "LG Remote X.Y" --notes "…"`
