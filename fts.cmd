rem FTS start script for Windows with 32bit Java
PATH=%PATH%;contrib/rxtx/win32
java -cp fts.jar;target/fts.jar;contrib/swt/swt-win32.jar;contrib/derby/derby.jar;contrib/rxtx/RXTXcomm.jar org.freebus.fts.Main
