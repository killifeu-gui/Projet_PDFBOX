@echo off
REM Script pour lancer le serveur CORBA
REM Assurez-vous que orbd est démarré

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
set PDFBOX_JAR=lib\pdfbox-2.0.29.jar
set FONTBOX_JAR=lib\fontbox-2.0.29.jar
set COMMONS_LOGGING_JAR=lib\commons-logging-1.2.jar
set CLASSPATH=bin;%PDFBOX_JAR%;%FONTBOX_JAR%;%COMMONS_LOGGING_JAR%

echo ========================================
echo Demarrage du serveur CORBA
echo ========================================

REM Démarrer ORBD en arrière-plan
start "ORBD" cmd /c "%JAVA_HOME%\bin\orbd.exe" -ORBInitialPort 1050

timeout /t 2 /nobreak >nul

REM Démarrer le serveur
"%JAVA_HOME%\bin\java" -cp %CLASSPATH% StartServer %*