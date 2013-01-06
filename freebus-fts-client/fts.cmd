@echo off
rem FTS start script for Windows with 32bit Java

set CLASSPATH=%CLASSPATH%;libs
java -jar libs\freebus-fts-client-*.jar -splash:libs/splash.png org.freebus.fts.client.FTS

if %ERRORLEVEL% neq 0 (
   echo.
   echo Please note: FTS requires Java 7
   echo (This might not be related to the problem above)
   echo.
   pause
)
