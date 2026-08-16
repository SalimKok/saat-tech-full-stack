# 1. Aşama: Build (Derleme) - Maven kullanarak projeyi derliyoruz
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Sadece bağımlılıkları indiriyoruz (Hızlandırmak için)
RUN mvn dependency:go-offline -B
COPY src ./src
# Projeyi build ediyoruz
RUN mvn clean package -DskipTests

# 2. Aşama: Run (Çalıştırma) - Sadece JRE kullanarak hafif bir imaj oluşturuyoruz
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 1. aşamada oluşan .jar dosyasını kopyalıyoruz
COPY --from=build /app/target/*.jar app.jar
# Güvenlik için yetkisiz bir kullanıcı oluşturuyoruz
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
