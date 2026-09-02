FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .

RUN mvn dependency:go-offline -B
COPY src ./src

RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir -p /app/uploads && chown -R spring:spring /app
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
