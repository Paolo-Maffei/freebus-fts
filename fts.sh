#!/bin/sh

topDir=`dirname $0`
test -d "$topDir" || topDir=.

# find RXTX system library
sysname="`uname -m`*"
rxtxdir=`ls -d "$topDir/contrib/rxtx"/$sysname`

FTS_JAR=`ls "$topDir"/fts*.jar "$topDir/target"/fts*.jar 2>/dev/null | head -n1`
CLASSPATH="$FTS_JAR:$topDir/contrib/swt/swt.jar:$topDir/contrib/derby/derby.jar:$topDir/contrib/rxtx/RXTXcomm.jar"

export LD_LIBRARY_PATH="$rxtxdir:$LD_LIBRARY_PATH"
set -x
java -cp "$CLASSPATH" org/freebus/fts/Main $*
