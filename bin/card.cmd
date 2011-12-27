@echo off

set CARD_DIR=%~dp0

java -jar "%CARD_DIR%\com.willmeyer.card-app-0.0.1-SNAPSHOT-jar-with-dependencies.jar" %*
