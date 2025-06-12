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

# ---------- Etapa 2: Imagen final Open Liberty con Chrome/ChromeDriver ----------
FROM openliberty/open-liberty:latest
USER root

# Instalar dependencias necesarias para Chrome
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    curl \
    xvfb \
    && rm -rf /var/lib/apt/lists/*

# Instalar Google Chrome (versión específica 137)
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable=137.0.7151.68-1 \
    && apt-mark hold google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Descargar e instalar ChromeDriver (versión específica 137.0.7151.70)
RUN CHROMEDRIVER_VERSION=137.0.7151.70 \
    && wget -O /tmp/chromedriver.zip "https://storage.googleapis.com/chrome-for-testing-public/$CHROMEDRIVER_VERSION/linux64/chromedriver-linux64.zip" \
    && unzip /tmp/chromedriver.zip -d /tmp/ \
    && mv /tmp/chromedriver-linux64/chromedriver /usr/bin/chromedriver \
    && chmod +x /usr/bin/chromedriver \
    && rm -rf /tmp/chromedriver*

# Verificar instalaciones
RUN google-chrome --version && chromedriver --version

# Crear directorios necesarios para Open Liberty
RUN mkdir -p /opt/ol/wlp/usr/servers/tpiserver_2025/dropins

# Copiamos el .war compilado desde la etapa anterior
COPY --from=build /build/target/PupasSv-1.0-SNAPSHOT.war /opt/ol/wlp/usr/servers/tpiserver_2025/dropins/

# Copiamos las dependencias necesarias (JDBC, config, etc)
COPY server.xml /opt/ol/wlp/usr/servers/tpiserver_2025/server.xml
COPY postgresql-42.7.5.jar /opt/ol/wlp/lib/

# Cambiar permisos si es necesario
RUN chown -R 1001:0 /opt/ol/wlp/usr/servers/tpiserver_2025/ && \
    chmod -R g+rw /opt/ol/wlp/usr/servers/tpiserver_2025/

# Crear un script para ejecutar con display virtual (útil para el pipeline)
RUN echo '#!/bin/bash\nxvfb-run -a --server-args="-screen 0 1920x1080x24" "$@"' > /usr/bin/run-with-xvfb \
    && chmod +x /usr/bin/run-with-xvfb

USER 1001

EXPOSE 9080 9443

CMD ["/opt/ol/wlp/bin/server", "run", "tpiserver_2025"]