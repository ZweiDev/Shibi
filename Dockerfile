FROM openjdk:15 AS BUILDER

COPY . .

RUN ./gradlew clean shibiJar

FROM openjdk:15-alpine
MAINTAINER tristan.marsell@axitera.de

RUN mkdir -p /home/app
WORKDIR /home/app
COPY --from=BUILDER /build/libs/shibi-1.0-SNAPSHOT.jar ./shibi-1.0-SNAPSHOT.jar

CMD ["java", "-jar", "shibi-1.0-SNAPSHOT.jar"]
