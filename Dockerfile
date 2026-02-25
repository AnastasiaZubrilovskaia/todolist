FROM maven:3.9.12-eclipse-temurin-21 AS build
WORKDIR /todolist
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /todolist
COPY --from=build /todolist/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]