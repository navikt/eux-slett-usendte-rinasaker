FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25
ENV TZ="Europe/Oslo"
COPY target/eux-slett-usendte-rinasaker.jar /app.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["java", "-jar", "/app.jar"]
