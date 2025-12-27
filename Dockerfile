FROM gcr.io/distroless/java25
COPY target/eux-slett-usendte-rinasaker.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
