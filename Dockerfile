FROM gradle:7.6.0-jdk-alpine AS build
COPY . .
RUN ./gradlew clean build --no-daemon

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
EXPOSE 8080
COPY --from=build /home/gradle/build/libs/demeter-0.0.1-SNAPSHOT.jar /app/
ENTRYPOINT ["java","-jar", "app/demeter-0.0.1-SNAPSHOT.jar"]