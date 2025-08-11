ARG BUILD_HOME=/app

FROM gradle:8.14-jdk21 AS build
ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME

WORKDIR $APP_HOME

COPY --chown=gradle:gradle build.gradle.kts settings.gradle.kts gradle.properties gradlew $APP_HOME/
COPY --chown=gradle:gradle gradle $APP_HOME/gradle
COPY --chown=gradle:gradle src $APP_HOME/src

RUN ./gradlew build --no-daemon


FROM eclipse-temurin:21-jre-alpine

ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME

WORKDIR $APP_HOME

COPY --from=build $APP_HOME/build/quarkus-app/ $APP_HOME

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=15s --retries=3 \
  CMD curl --fail --silent http://localhost:8080/q/health/ready \
   && curl --fail --silent http://localhost:8080/q/health/live || exit 1

CMD ["java", "-jar", "quarkus-run.jar"]