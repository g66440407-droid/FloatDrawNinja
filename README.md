# Dibujador flotante para NinjaTrader Mobile (Android)

Proyecto Android nativo en Kotlin. Muestra una pequeña barra flotante y una capa transparente sobre otras aplicaciones. Está pensado para uso vertical en un celular.

## Funciones
- Línea horizontal amarilla: tocá el gráfico para marcar un nivel.
- Trazo libre amarillo: dibujá con el dedo.
- PASAR: desactiva la capa de dibujo para poder tocar/usar NinjaTrader con normalidad.
- DESHACER, BORRAR y SALIR.
- Barra flotante movible: arrastrala desde una zona vacía de la barra.

## Limitaciones importantes
- Es una capa de dibujo de pantalla, no una integración con NinjaTrader.
- No conoce los precios ni convierte una línea de pantalla en un nivel de precio.
- Mientras el modo de dibujo está activo, los toques sobre el gráfico dibujan y no operan el gráfico. Tocá PASAR para recuperar los toques normales.
- Los dibujos viven en memoria y se pierden si se cierra o reinicia el servicio/app.
- Android puede restringir superposiciones en ciertas pantallas sensibles o según el fabricante.

## Compilar e instalar
1. En una computadora, instalá Android Studio desde https://developer.android.com/studio.
2. Descomprimí el ZIP y abrí la carpeta `FloatDrawNinja` en Android Studio.
3. Aceptá la instalación de Android SDK Platform 35 y deja que Gradle termine la sincronización.
4. Conectá el teléfono por USB y activá Opciones de desarrollador > Depuración USB, o compilá un APK desde Build > Build Bundle(s) / APK(s) > Build APK(s).
5. El APK de depuración queda normalmente en `app/build/outputs/apk/debug/app-debug.apk`. Copialo al celular e instalalo. Si Android lo pide, permití instalar aplicaciones desde esa fuente.
6. Abrí “Dibujador flotante”, tocá “Dar permiso de superposición” y habilitá “Mostrar sobre otras apps”. Volvé y tocá “Iniciar dibujador flotante”.
7. Abrí NinjaTrader Mobile. Usá LÍNEA H o TRAZO para dibujar. Tocá PASAR para usar el gráfico normalmente. Para quitar la herramienta, tocá SALIR.

## Seguridad de uso
Usá PASAR antes de desplazarte por el gráfico, cambiar instrumentos o tocar botones de compra/venta. La capa es manual y puede cubrir parte de la pantalla; mové la barra a un borde que no tape controles importantes.
