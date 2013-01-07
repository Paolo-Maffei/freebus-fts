@echo off
setlocal

cd libs
set CLASSPATH=%CLASSPATH%;.
java -jar freebus-fts-client-0.2-SNAPSHOT.jar -splash:splash.png org.freebus.fts.client.FTS

if %ERRORLEVEL% neq 0 (
   echo.
   echo Please note: FTS requires Java 7
   echo (This might not be related to the problem above)
   echo.
   pause
)
