@echo off
REM Script de compilation pour TP_Corba
REM Utilise Java 1.8 et PDFBox 2.0.29

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
set PDFBOX_JAR=lib\pdfbox-2.0.29.jar
set FONTBOX_JAR=lib\fontbox-2.0.29.jar
set COMMONS_LOGGING_JAR=lib\commons-logging-1.2.jar
set CLASSPATH=%PDFBOX_JAR%;%FONTBOX_JAR%;%COMMONS_LOGGING_JAR%;.

echo ========================================
echo Compilation du projet TP_Corba
echo ========================================

REM Créer le dossier bin s'il n'existe pas
if not exist bin mkdir bin

echo [1/3] Compilation des fichiers CalculatriceApp...
"%JAVA_HOME%\bin\javac" -cp %CLASSPATH% -d bin CalculatriceApp\*.java
if errorlevel 1 (
    echo ERREUR: Echec compilation CalculatriceApp
    exit /b 1
)

echo [2/3] Compilation du serveur...
"%JAVA_HOME%\bin\javac" -cp %CLASSPATH% -d bin src\CalculatriceServer\*.java
if errorlevel 1 (
    echo ERREUR: Echec compilation serveur
    exit /b 1
)

echo [3/3] Compilation des clients...
"%JAVA_HOME%\bin\javac" -cp %CLASSPATH% -d bin src\CalculatriceClient\*.java
if errorlevel 1 (
    echo ERREUR: Echec compilation clients
    exit /b 1
)

echo ========================================
echo Compilation terminee avec succes!
echo ========================================
echo.
echo Fichiers generes dans bin/:
dir /b bin