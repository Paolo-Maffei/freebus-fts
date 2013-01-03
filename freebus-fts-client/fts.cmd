rem FTS start script for Windows with 32bit Java

PATH=%PATH%;contrib/rxtx/win32
java -cp "libs/*" -splash:libs/splash.png org.freebus.fts.client.FTS
