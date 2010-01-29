#!/bin/sh

topDir=`dirname $0`
if [ -n "$topDir" ]; then
   cd "$topDir" || (echo "Cannot chdir into $topDir"; exit 1)
fi

classpath=""
for jar in `ls libs/*.jar`; do
   classpath="$classpath:$jar"
done

set -x
java -cp "$classpath" org.freebus.fts.FTS $*
