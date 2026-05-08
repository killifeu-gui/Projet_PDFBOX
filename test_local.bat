@echo off
REM Script de test local pour TP_Corba
REM Ce script démarre ORBD, le serveur, puis un client de test

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
set PDFBOX_JAR=lib\pdfbox-2.0.29.jar
set FONTBOX_JAR=lib\fontbox-2.0.29.jar
set COMMONS_LOGGING_JAR=lib\commons-logging-1.2.jar
set CLASSPATH=bin;%PDFBOX_JAR%;%FONTBOX_JAR%;%COMMONS_LOGGING_JAR%

echo ========================================
echo TEST LOCAL - TP_Corba
echo ========================================
echo.

echo [1/4] Arret des processus Java existants...
taskkill /F /IM java.exe /T 2>nul
taskkill /F /IM orbd.exe /T 2>nul
timeout /t 1 /nobreak >nul

echo [2/4] Demarrage de ORBD (Naming Service)...
start "ORBD" cmd /c "%JAVA_HOME%\bin\orbd.exe" -ORBInitialPort 1050
timeout /t 2 /nobreak >nul

echo [3/4] Demarrage du serveur CORBA...
start "Serveur" cmd /c "%JAVA_HOME%\bin\java.exe" -cp %CLASSPATH% StartServer -ORBInitialHost localhost -ORBInitialPort 1050
timeout /t 3 /nobreak >nul

echo [4/4] Verification...
echo.
echo Processus en cours :
tasklist | findstr /i "java orbd"
echo.

if exist ior.txt (
    echo ✓ Serveur demarre avec succes !
    echo ✓ Fichier ior.txt genere.
    echo.
    echo Pour tester le client calculatrice, executez :
    echo   java -cp %CLASSPATH% StartClient -ORBInitialHost localhost -ORBInitialPort 1050
    echo.
    echo Pour tester un client PDF (extraction texte sur bab's.pdf) :
    echo   java -cp %CLASSPATH% StartPdfClientConversionEtTexte "bab's.pdf" output.png 150
    echo.
) else (
    echo ✗ Erreur: ior.txt non genere.
)

echo Appuyez sur une touche pour continuer...
pause >nul