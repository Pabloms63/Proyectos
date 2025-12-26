@echo off

REM ========== CONFIGURACIÓN DE LIBRERÍAS ==========
REM Todas las librerías necesarias separadas por punto y coma
set LIBS=libs\gdx-1.12.0.jar;libs\gdx-backend-lwjgl3-1.12.0.jar;libs\gdx-freetype-1.12.0.jar;libs\gdx-freetype-platform-1.12.0-natives-desktop.jar;libs\gdx-liftoff-1.13.1.0.jar;libs\gdx-platform-1.12.0-natives-desktop.jar;libs\sqlite-jdbc-3.49.1.0.jar

REM ========== RUTA A DLLs NATIVAS ==========
set NATIVES=dist\natives

REM ========== EJECUTAR ==========
REM -cp incluye tu .jar + las librerías
REM -Djava.library.path define la carpeta con DLLs como glfw.dll, stb.dll, etc
java -Djava.library.path=%NATIVES% -cp target\proyecto-TFG-0.0.1-SNAPSHOT.jar;%LIBS% tfg.Main

pause
