# Step 1: Build stage
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory for the build
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the entire project
COPY . .

# Build the application using Maven
RUN mvn clean package -DskipTests

# Step 2: Runtime stage
FROM openjdk:17-jdk-slim

# Set the working directory for the runtime environment
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar application.jar

# Expose the application port (adjust as necessary)
EXPOSE 8080

# Set the entry point for running the application
ENTRYPOINT ["java", "-jar", "application.jar"]