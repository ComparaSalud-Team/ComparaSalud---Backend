FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY pom.xml ./
COPY mvnw ./
RUN chmod +x mvnw

COPY src ./src

RUN ./mvnw clean package -DskipTests -B

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "target/ComparaSalud-0.0.1-SNAPSHOT.jar"]