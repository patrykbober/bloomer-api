# 🌻 Bloomer API

## Description

Bloomer backend application developed using Java 17, Spring Boot and PostgreSQL.

## Try me

### 1. docker-compose Postgres setup

Run ```docker-compose up``` or ```docker-compose up -d``` (detached mode) in order to set up the local database.

### 2. Spring Boot app

- add "local" in Run/Debug Configurations -> Active profiles and run the app in IntelliJ, or
- run ```mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"```
