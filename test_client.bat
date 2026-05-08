@echo off
REM Test d'un client PDF avec le serveur CORBA

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
set PDFBOX_JAR=lib\pdfbox-2.0.29.jar
set FONTBOX_JAR=lib\fontbox-2.0.29.jar
set COMMONS_LOGGING_JAR=lib\commons-logging-1.2.jar
set CLASSPATH=bin;%PDFBOX_JAR%;%FONTBOX_JAR%;%COMMONS_LOGGING_JAR%

echo ========================================
echo Test Client PDF - Extraction texte + Conversion image
echo ========================================
echo.

if not exist "bab's.pdf" (
    echo Erreur: Le fichier bab's.pdf n'existe pas !
    exit /b 1
)

echo Fichier source: bab's.pdf
echo.

echo Lancement du client StartPdfClientConversionEtTexte...
"%JAVA_HOME%\bin\java.exe" -cp %CLASSPATH% StartPdfClientConversionEtTexte "bab's.pdf" output.png 150 -ORBInitialHost localhost -ORBInitialPort 1050

echo.
if exist output.png (
    echo ✓ Succes! Image generee: output.png
    dir output.png
) else (
    echo ✗ Echec: output.png non genere
)

echo.
if exist ior.txt (
    echo ✓ Serveur CORBA fonctionne (ior.txt present)
) else (
    echo ✗ Serveur CORBA non detecte
)