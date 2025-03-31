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
public class OrdenResourceSI  extends  AbstractContainerTest {


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
