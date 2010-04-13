#!/bin/bash
exec ./liquibase --classpath=../freebus-fts-products/target/classes:../freebus-fts-project/target/classes:/lhome/taf/.m2/repository/mysql/mysql-connector-java/5.1.6/mysql-connector-java-5.1.6.jar \
  --driver=com.mysql.jdbc.Driver \
  --changeLogFile=db.changelog-master.xml \
  --url="jdbc:mysql://localhost/fts" \
  --username=fts --password=abc123 \
  update

