FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests -B

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "target/ComparaSalud-0.0.1-SNAPSHOT.jar"]