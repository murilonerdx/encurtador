# Etapa de Build
FROM openjdk:17-jdk-slim AS build
RUN apt-get update && apt-get install -y maven
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa Final
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/encurtador-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
