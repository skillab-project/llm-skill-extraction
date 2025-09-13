# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
#RUN mvn clean install
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy only the built jar from the builder stage
COPY --from=build /app/target/CitizensV1-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
