package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.OrdenDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Orden;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Pago;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.PagoDetalle;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PagoResourceSI extends AbstractContainerTest {

    private Long testOrdenId = 1431L; // ID de orden existente para pruebas

    @Test
    @Order(1)
    public void testFindRange() {
        System.out.println("PagoResource.findRange");
        Response response = webTarget.path("pago")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        List<Pago> pagos = response.readEntity(new GenericType<List<Pago>>() {});
        assertNotNull(pagos);
    }

    @Test
    @Order(2)
    public void testCreate() {
        System.out.println("PagoResource.create");

        Orden orden = new Orden();
        orden.setFecha(new Date());
        orden.setSucursal("TEST");
        orden.setAnulada(false);

        Response response = webTarget.path("orden")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(orden, MediaType.APPLICATION_JSON));

        //Crea pago sin una orden
        Pago pagoMal = new Pago();
        pagoMal.setFecha(new Date());
        pagoMal.setMetodoPago("EFECTIVO");

        response = webTarget.path("pago")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(pagoMal, MediaType.APPLICATION_JSON));
        assertEquals(400, response.getStatus());
    }

    @Test
    @Order(3)
    public void testFindById() {
        System.out.println("PagoResource.findById");

        Pago pago = new Pago();
        pago.setFecha(new Date());
        pago.setMetodoPago("EFECTIVO");
        pago.setReferencia("TEST-FIND");
        pago.setIdOrden(new Orden(testOrdenId));

        Response response = webTarget.path("pago")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(pago, MediaType.APPLICATION_JSON));

        String location = response.getLocation().toString();
        Long pagoId = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        // Ahora lo buscamos
        response = webTarget.path("pago")
                .path(pagoId.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(200, response.getStatus());
        Pago pagoEncontrado = response.readEntity(Pago.class);
        assertNotNull(pagoEncontrado);
        assertEquals(pagoId, pagoEncontrado.getIdPago());

        //Pago que no existe
        response = webTarget.path("pago")
                .path("999999") // ID que no existe
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(404, response.getStatus());
    }

    /**
     * Prueba que verifica la actualización de un pago existente.
     */
    @Test
    @Order(4)
    public void testUpdate() {
        System.out.println("PagoResource.update");
        Pago pago = new Pago();
        pago.setFecha(new Date());
        pago.setMetodoPago("EFECTIVO");
        pago.setReferencia("TEST-UPDATE");
        pago.setIdOrden(new Orden(testOrdenId));
        Response response = webTarget.path("pago")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(pago, MediaType.APPLICATION_JSON));
        String location = response.getLocation().toString();
        Long pagoId = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
        // Actualiza el pago
        pago.setIdPago(pagoId);
        pago.setMetodoPago("TARJETA");
        pago.setReferencia("ACTUALIZADO");
        response = webTarget.path("pago")
                .path(pagoId.toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(pago, MediaType.APPLICATION_JSON));

        assertEquals(200, response.getStatus());
        Pago pagoActualizado = response.readEntity(Pago.class);
        assertEquals("TARJETA", pagoActualizado.getMetodoPago());
        assertEquals("ACTUALIZADO", pagoActualizado.getReferencia());
    }

    @Test
    @Order(5)
    public void testDelete() {
        System.out.println("PagoResource.delete");
        Pago pago = new Pago();
        pago.setFecha(new Date());
        pago.setMetodoPago("EFECTIVO");
        pago.setReferencia("TEST-DELETE");
        pago.setIdOrden(new Orden(testOrdenId));
        Response response = webTarget.path("pago")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(pago, MediaType.APPLICATION_JSON));

        String location = response.getLocation().toString();
        Long pagoId = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        // Eliminar el pago
        response = webTarget.path("pago")
                .path(pagoId.toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(204, response.getStatus());
        // Verifica que ya no existe
        response = webTarget.path("pago")
                .path(pagoId.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(404, response.getStatus());
    }
}