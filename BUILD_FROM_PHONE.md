# Generar el APK usando solo el celular (GitHub Actions)

Este proyecto no trae un APK ya compilado. Este flujo permite compilarlo en los servidores de GitHub desde el navegador del teléfono.

1. En el navegador, entra en https://github.com/new e inicia sesión o crea una cuenta.
2. Crea un repositorio nuevo llamado `FloatDrawNinja`. Puedes marcarlo **Private**. No hace falta añadir README, licencia ni `.gitignore`.
3. Abre el repositorio y pulsa **Add file → Upload files**.
4. Extrae el ZIP original en el teléfono. En el repositorio debes subir el CONTENIDO de la carpeta `FloatDrawNinja`, incluyendo la carpeta oculta `.github`. Si el selector no muestra archivos ocultos, usa un gestor de archivos que permita mostrar archivos ocultos o crea `.github/workflows/build-apk.yml` desde GitHub web y pega el contenido del archivo incluido en este ZIP.
5. Pulsa **Commit changes** para guardar los archivos.
6. Abre la pestaña **Actions** del repositorio. Si aparece una confirmación para habilitar workflows, aceptala.
7. Selecciona **Build FloatDrawNinja APK** y pulsa **Run workflow** (si el workflow ya se ejecutó con el commit, abre esa ejecución).
8. Espera a que la ejecución termine con marca verde. En la parte inferior de la ejecución, busca **Artifacts** y descarga `FloatDrawNinja-debug-apk`.
9. Extrae ese ZIP descargado para obtener `app-debug.apk`. Tócalo para instalarlo. Android puede pedir que permitas instalar aplicaciones desde el navegador o gestor de archivos que usaste.

Nota: el primer build puede fallar si el código requiere ajustes o por cambios en herramientas. Si falla, abre la ejecución y comparte una captura del error.
