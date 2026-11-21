# Mando TV

Mando a distancia para Android, sin anuncios, para la tele LG 42LW5500 (2012, no Smart TV).
Funciona por **infrarrojos**: hace falta un móvil con emisor IR (muchos Xiaomi lo llevan).

## Cómo funciona

- Las LG usan el protocolo **NEC** a 38 kHz con la dirección `0x04`. Cada tecla es un número de orden
  (`ir/LgKey.kt`); `ir/Nec.kt` lo convierte en el patrón de pulsos que emite `ConsumerIrManager`.
- Volumen, canal y flechas se repiten al mantener pulsado (cada 110 ms, tras 400 ms).
  `ir/KeySender.kt` descarta repeticiones si el emisor va atrasado, para que al soltar se pare en seco.
- Interfaz en Jetpack Compose (`ui/`), oscura, pensada para usarse con una mano.

## Compilar e instalar

```sh
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew testDebugUnitTest assembleRelease
# APK: app/build/outputs/apk/release/app-release.apk
```

El APK de release se firma con la clave de depuración de este Mac: para actualizar la app
instalada hay que compilarla en este mismo Mac (si no, Android pide desinstalar la anterior).

Con el móvil conectado por USB: `adb install -r app/build/outputs/apk/release/app-release.apk`.

## Teclas por comprobar

Los códigos principales (encendido, volumen, canal, números, flechas, OK, atrás, salir, menú,
colores, input, silencio) están contrastados en los tests con las tablas públicas de LG.
Los secundarios (Q.MENU, INFO, GUIDE, FAV, RATIO, 3D, AV MODE, SUBT., TEXT, reproducción)
salen de esas mismas tablas, pero hay que probarlos en la tele.
