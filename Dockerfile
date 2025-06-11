# ---------- Etapa 1: Compilación con Maven ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# ---------- Etapa 2: Imagen final Open Liberty ----------
FROM openliberty/open-liberty:latest

USER root

RUN mkdir -p /opt/ol/wlp/usr/servers/tpiserver_2025/dropins

# Copiamos el .war compilado desde la etapa anterior
COPY --from=build /build/target/PupasSv-1.0-SNAPSHOT.war /opt/ol/wlp/usr/servers/tpiserver_2025/dropins/PupasSv-1.0-SNAPSHOT.war

# Copiamos las dependencias necesarias (JDBC, config, etc)
COPY server.xml /opt/ol/wlp/usr/servers/tpiserver_2025/server.xml
COPY postgresql-42.7.5.jar /opt/ol/wlp/lib/postgresql-42.7.5.jar

USER 1001

EXPOSE 9080 9443

CMD ["/opt/ol/wlp/bin/server", "run", "tpiserver_2025"]
