FROM gradle:jdk17-alpine as gradleimage

WORKDIR /home/app

COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN gradle shadowjar

FROM gcr.io/distroless/java17-debian11

COPY --from=gradleimage /home/gradle/source/build/libs/apollo-all.jar /home/app/apollo.jar

WORKDIR /home/app

ENTRYPOINT ["java","-jar","apollo.jar"]
