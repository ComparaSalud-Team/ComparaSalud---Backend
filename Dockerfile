FROM eclipse-temurin:21-jdk-jammy

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests -B

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "target/ComparaSalud-0.0.1-SNAPSHOT.jar"]