FROM openjdk:17-jdk-slim-buster
WORKDIR /app

EXPOSE 8080

COPY /build/libs/vcs-repository-search.jar build/app.jar

WORKDIR /app/build
ENTRYPOINT java -jar app.jar
