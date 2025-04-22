package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import java.nio.file.Paths;

/**
 * Clase base para pruebas de integración utilizando Testcontainers.
 *
 * Esta clase proporciona un contenedor de PostgreSQL y un contenedor de
 * OpenLiberty
 * configurados con una red compartida para realizar pruebas de integración.
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractContainerTest {

        /** Cliente utilizado para realizar solicitudes HTTP en las pruebas. */
        protected Client client;

        /** Define adonde se van hacer las peticiones rest. */
        protected WebTarget webTarget;

        /** Red compartida a la que se conectan los 2 servidores */
        protected static Network red = Network.newNetwork();

        /**
         * Contenedor PostgreSQL configurado con Testcontainers.
         * Se inicializa con un script SQL para crear las tablas necesarias.
         */
        @Container
        protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                        .withDatabaseName("PupasBd_tpi2025")
                        .withUsername("postgres")
                        .withPassword("abc123")
                        .withNetwork(red)
                        .withNetworkAliases("db16")
                        .withInitScript("pupas_ddl.sql");

        /**
         * Archivo WAR de la aplicación a desplegar en Open Liberty.
         */
        private static MountableFile war = MountableFile
                        .forHostPath(Paths.get("target/PupasSv-1.0-SNAPSHOT.war").toAbsolutePath());

        /**
         * Contenedor Open Liberty configurado para desplegar el WAR de la aplicación
         * y conectado a la base de datos PostgreSQL por medio de red
         */
        @Container
        protected static final GenericContainer<?> openliberty = new GenericContainer<>(
                        "openliberty/open-liberty:latest")
                        .withExposedPorts(9080)
                        .withCopyFileToContainer(war,
                                        "/opt/ol/wlp/usr/servers/defaultServer/dropins/PupasSv-1.0-SNAPSHOT.war")
                        .withNetwork(red)
                        .withEnv("PGPASSWORD", "abc123")
                        .withEnv("PGUSER", "postgres")
                        .withEnv("PGDBNAME", "PupasBd_tpi2025")
                        .withEnv("PGPORT", "5432")
                        .withEnv("PGSERVER", "db16")
                        .dependsOn(postgres)
                        .waitingFor(Wait.forLogMessage(".*server is ready to run a smarter planet.*", 1));

        /**
         * Método de inicialización que configura el cliente HTTP y la URL base para las
         * pruebas.
         */
        @BeforeAll
        public void init() {
                client = ClientBuilder.newClient();
                webTarget = client.target(String.format(
                                "http://%s:%d/PupasSv-1.0-SNAPSHOT/v1/",
                                openliberty.getHost(),
                                openliberty.getMappedPort(9080)));
        }
}
