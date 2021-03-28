FROM gcr.io/distroless/java:11
WORKDIR /app
COPY build/libs/Apollo.jar /app/Apollo.jar
CMD [ "Apollo.jar" ]