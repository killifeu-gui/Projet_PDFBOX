@echo off
REM Client CORBA pour fusion de PDFs
REM Usage: run_client_fusion pdf1.pdf pdf2.pdf output.pdf

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
set PDFBOX_JAR=lib\pdfbox-2.0.29.jar
set FONTBOX_JAR=lib\fontbox-2.0.29.jar
set COMMONS_LOGGING_JAR=lib\commons-logging-1.2.jar
set CLASSPATH=bin;%PDFBOX_JAR%;%FONTBOX_JAR%;%COMMONS_LOGGING_JAR%;.

"%JAVA_HOME%\bin\java" -cp %CLASSPATH% CalculatriceClient.ClientFusion %*