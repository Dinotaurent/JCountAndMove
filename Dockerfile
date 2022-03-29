FROM openjdk:19-slim-buster
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} CountAndMove-1.0.jar