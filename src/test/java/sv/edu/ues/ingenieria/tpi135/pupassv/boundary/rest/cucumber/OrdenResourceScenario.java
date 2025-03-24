package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.MountableFile;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Orden;

import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.Assert.fail;

public class OrdenResourceScenario {

    Client client;
    WebTarget webTarget;
    static Network red = Network.newNetwork();

    static GenericContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("PupasBd_tpi2025")
            .withPassword("abc123")
            .withUsername("postgres")
            .withInitScript("pupas_ddl.sql")
            .withExposedPorts(5432)
            .withNetwork(red)
            .withNetworkAliases("db16");

    static MountableFile war = MountableFile.forHostPath(
            Paths.get("target/PupasSv-1.0-SNAPSHOT.war").toAbsolutePath()
    );

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

    @When("Se tiene un servidor openliberty corriendo con una pliacion desplegada")
    public void lanzarOpenLiberty() {
        Startables.deepStart(Stream.of(postgres,openliberty)).join();
        Assertions.assertTrue(openliberty.isRunning());
        client = ClientBuilder.newClient();
        webTarget = client.target(String.format("http://localhost:%d/PupasSv-1.0-SNAPSHOT/v1/", openliberty.getMappedPort(9080)));
    }

    @Then("los usuarios hacen POST enviando una Orden con payload en formato JSON, el servidor deberia contestar\n" +
            " con un estado (int) e incluir una cabecera location apuntando al registro creado")
    public void crearOrden(Integer codigo){
        System.out.println("Creando Orden");
        Orden orden = new Orden();
        orden.setAnulada(false);
        orden.setFecha(new Date());
        orden.setSucursal("ZARZA");
        Response response = webTarget
                .path("orden")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(orden, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(codigo, response.getStatus());
        Assertions.assertTrue(response.getHeaders().containsKey("location"));
        Assertions.assertNotNull(Integer.valueOf(response.getHeaderString("location").split("orden/")[1]));

        fail("No pasa quemado");

    }

}
