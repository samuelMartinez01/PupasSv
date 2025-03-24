package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Orden;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración para el recurso RESTful de la entidad {@link Orden}.
 * Utiliza Testcontainers para levantar un contenedor de PostgreSQL y un contenedor de Open Liberty
 * que despliega la aplicación.
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrdenResourceIT {

    /**
     * Cliente HTTP reutilizable para invocar el endpoint REST.
     */
    Client client;

    /**
     * Representa la URL base sobre la que se ejecutan las solicitudes HTTP.
     */
    WebTarget webTarget;

    /**
     * Red compartida entre los contenedores PostgreSQL y Open Liberty.
     */
    static Network red = Network.newNetwork();

    /**
     * Contenedor PostgreSQL configurado con Testcontainers.
     * Se inicializa con un script SQL para crear las tablas necesarias.
     */
    @Container
    static GenericContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("PupasBd_tpi2025")
            .withPassword("abc123")
            .withUsername("postgres")
            .withInitScript("pupas_ddl.sql")
            .withExposedPorts(5432)
            .withNetwork(red)
            .withNetworkAliases("db16");

    /**
     * Archivo WAR de la aplicación a desplegar en Open Liberty.
     */
    static MountableFile war = MountableFile.forHostPath(
            Paths.get("target/PupasSv-1.0-SNAPSHOT.war").toAbsolutePath()
    );

    /**
     * Contenedor Open Liberty configurado para desplegar el WAR de la aplicación
     * y conectado a la base de datos PostgreSQL.
     */
    @Container
    static GenericContainer openliberty = new GenericContainer("openliberty/open-liberty:latest")
            .withExposedPorts(9080)
            .withCopyFileToContainer(war, "/opt/ol/wlp/usr/servers/defaultServer/dropins/PupasSv-1.0-SNAPSHOT.war")
            .withNetwork(red)
            .withEnv("PGPASSWORD", "abc123")
            .withEnv("PGUSER", "postgres")
            .withEnv("PGDBNAME", "PupasBd_tpi2025")
            .withEnv("PGPORT", "5432")
            .withEnv("PGSERVER", "db16")
            .dependsOn(postgres)
            .waitingFor(Wait.forLogMessage(".*server is ready to run a smarter planet.*", 1));

    /**
     * Método de inicialización que configura el cliente HTTP y la URL base para las pruebas.
     */
    @BeforeAll
    public void init() {
        client = ClientBuilder.newClient();
        webTarget = client.target(String.format("http://localhost:%d/PupasSv-1.0-SNAPSHOT/v1/", openliberty.getMappedPort(9080)));
    }

    /**
     * Prueba que verifica el correcto funcionamiento del endpoint que retorna un rango de órdenes.
     * Se asegura que el contenedor esté corriendo, que el endpoint responda correctamente
     * y que el número de órdenes retornadas sea el esperado.
     */
@Test
public void findRange() {
    System.out.println("findRange");
    System.out.println("Contenedor corriendo: " + openliberty.isRunning());

    // Verifica que el contenedor de Open Liberty esté en ejecución.
    assertTrue(openliberty.isRunning());

    // Realiza una solicitud GET al endpoint 'orden' y verifica la respuesta.
    Response response = webTarget.path("orden").request(MediaType.APPLICATION_JSON).get();

    // Validaciones sobre la respuesta.
    assertNotNull(response);
    assertEquals(200, response.getStatus());

    // Procesa el cuerpo de la respuesta y valida el número de registros.
    List<Orden> ordens = response.readEntity(new GenericType<List<Orden>>() {});
    assertNotNull(ordens);
    assertEquals(3, ordens.size());  // Se espera que el script de inicialización cree 3 órdenes.
}

}
