# Use Maven with Java 21 for development
FROM maven:3.9-eclipse-temurin-21

# Set working directory
WORKDIR /app

# The source code will be mounted as a volume in docker-compose.yml,
# but we can optionally copy pom.xml to download dependencies first to cache them.
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Run the Spring Boot development server with hot reload
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'"]
