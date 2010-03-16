#!/bin/bash

LIQUIBASE_MAIN=liquibase.commandline.Main

LIQUIBASE_JAR=`ls -rt liquibase*.jar|head -n1`
#LIQUIBASE_JAR=`ls -rt $HOME/.m2/repository/org/liquibase/liquibase-core/*/*.jar | head -n1`
MYSQL_JAR=`ls -rt $HOME/.m2/repository/mysql/mysql-connector-java/*/*.jar | head -n1`

CP="$LIQUIBASE_JAR:$MYSQL_JAR:../target/classes"

exec java -cp "$CP" $LIQUIBASE_MAIN --driver=com.mysql.jdbc.Driver \
   --changeLogFile=db.changelog.xml --url="jdbc:mysql://localhost/fts" \
   --username=fts --password=abc123 $* generateChangeLog
