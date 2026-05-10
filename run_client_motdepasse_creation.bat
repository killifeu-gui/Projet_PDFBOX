@echo off
REM Script pour executer le client PDF d'ajout de mot de passe et de creation

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
set PDFBOX_JAR=lib\pdfbox-2.0.29.jar
set FONTBOX_JAR=lib\fontbox-2.0.29.jar
set COMMONS_LOGGING_JAR=lib\commons-logging-1.2.jar
set CLASSPATH=%PDFBOX_JAR%;%FONTBOX_JAR%;%COMMONS_LOGGING_JAR%;bin

echo ========================================
echo Execution du client PDF - Mot de passe/Creation
echo ========================================

"%JAVA_HOME%\bin\java" -cp %CLASSPATH% StartPdfClientMotDePasseEtCreation %*
