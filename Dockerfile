FROM openjdk:17-alpine

ARG JAR_FILE=*.jar
COPY target/${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]