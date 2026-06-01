FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S architect && adduser -S architect -G architect
COPY --from=build /app/target/knowledgestack-platform.jar ./app.jar
RUN chown -R architect:architect /app
USER architect
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
