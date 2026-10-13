FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.32.0/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

EXPOSE 8080
EXPOSE 5005

CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments=-javaagent:/otel/opentelemetry-javaagent.jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"]