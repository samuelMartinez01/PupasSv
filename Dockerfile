# ---------- Etapa 1: Compilación con Maven ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build
# Crear directorio de trabajo
WORKDIR /build
# Copiar solo los archivos de configuración primero (para aprovechar cache de Docker)
COPY pom.xml .
# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B
# Copiar el resto del código fuente
COPY src ./src
# Compilar la aplicación
RUN mvn clean package -DskipTests
# ---------- Etapa 2: Imagen final Open Liberty ----------
FROM openliberty/open-liberty:latest
USER root
# Crear directorios necesarios
RUN mkdir -p /opt/ol/wlp/usr/servers/tpiserver_2025/dropins
# Copiamos el .war compilado desde la etapa anterior
COPY --from=build /build/target/PupasSv-1.0-SNAPSHOT.war /opt/ol/wlp/usr/servers/tpiserver_2025/dropins/
# Copiamos las dependencias necesarias (JDBC, config, etc)
COPY server.xml /opt/ol/wlp/usr/servers/tpiserver_2025/server.xml
COPY postgresql-42.7.5.jar /opt/ol/wlp/lib/
# Cambiar permisos si es necesario
RUN chown -R 1001:0 /opt/ol/wlp/usr/servers/tpiserver_2025/ && \
    chmod -R g+rw /opt/ol/wlp/usr/servers/tpiserver_2025/
USER 1001
EXPOSE 9080 9443
CMD ["/opt/ol/wlp/bin/server", "run", "tpiserver_2025"]