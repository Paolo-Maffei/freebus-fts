#!/bin/sh

topDir=`dirname $0`
if [ -n "$topDir" ]; then
   cd "$topDir" || (echo "Cannot chdir into $topDir"; exit 1)
fi

set -x
java -cp 'libs/*' -splash:libs/splash.png org.freebus.fts.client.FTS $*
