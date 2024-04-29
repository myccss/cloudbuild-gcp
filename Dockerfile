FROM openjdk:19-jdk-alpine

MAINTAINER  mycc


WORKDIR /data
COPY  javacontainer-1.0.1-SNAPSHOT.jar  /data/app.jar
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
