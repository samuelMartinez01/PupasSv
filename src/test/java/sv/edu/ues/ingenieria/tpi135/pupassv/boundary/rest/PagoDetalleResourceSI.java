package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Orden;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Pago;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.PagoDetalle;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PagoDetalleResourceSI extends AbstractContainerTest {
    private Long testOrdenId = 1431L; // ID de orden en la bd

    @Test
    @Order(1)
    public void testAddDetalle() {
        System.out.println("PagoResource.addDetalle");
        Pago pago = new Pago();
        pago.setFecha(new Date());
        pago.setMetodoPago("EFECTIVO");
        pago.setReferencia("TEST-DETALLE");
        pago.setIdOrden(new Orden(testOrdenId));

        Response pagoResponse = webTarget.path("pago")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(pago, MediaType.APPLICATION_JSON));

        String location = pagoResponse.getLocation().toString();
        Long pagoId = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));

        PagoDetalle detalle = new PagoDetalle();
        detalle.setMonto(new BigDecimal("50.00"));
        detalle.setObservaciones("Parcial por transferencia");

        Response response = webTarget.path("pago")
                .path(pagoId.toString())
                .path("detalle")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(detalle, MediaType.APPLICATION_JSON));
        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        //Add a un pago que no existe
        Response notFoundResponse = webTarget.path("pago")
                .path("999999") // ID que no existe
                .path("detalle")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(detalle, MediaType.APPLICATION_JSON));

        assertEquals(404, notFoundResponse.getStatus());
    }

    @Test
    @Order(2)
    public void testGetDetalles() {
        System.out.println("PagoResource.getDetalles");
        Long pagoId = 1L; //EN la bd
        Response response = webTarget.path("pago")
                .path(pagoId.toString())
                .path("detalle")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(200, response.getStatus());
        List<PagoDetalle> detalles = response.readEntity(new GenericType<List<PagoDetalle>>() {});
        assertNotNull(detalles);
        assertEquals(2, detalles.size());

        Response notFoundResponse = webTarget.path("pago")
                .path("999999") // ID que no existe
                .path("detalle")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(404, notFoundResponse.getStatus());
    }

    @Test
    @Order(3)
    public void testDeleteDetalle() {
        System.out.println("PagoDetalleResource.deleteDetalle");
        Long pagoId = 1L; //Tiene 2 detalles
        Long detalleId = 1L;
        // Eliminar un detalle de los 2
        Response deleteResponse = webTarget.path("pago")
                .path(pagoId.toString())
                .path("detalle")
                .path(detalleId.toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(204, deleteResponse.getStatus());
        Response response = webTarget.path("pago")
                .path(pagoId.toString())
                .path("detalle")
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(200, response.getStatus());
        List<PagoDetalle> detalles = response.readEntity(new GenericType<List<PagoDetalle>>() {});
        assertNotNull(detalles);
        assertEquals(1, detalles.size());
        // Caso de error: Detalle que no existe
        Response notFoundResponse = webTarget.path("pago")
                .path(pagoId.toString())
                .path("detalle")
                .path("999999")
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(404, notFoundResponse.getStatus());
    }

    @Test
    @Order(4)
    public void testUpdateDetalle() {
        System.out.println("PagoDetalleResource.updateDetalle");
        Long pagoId =2L;
        Long detalleId = 3L;
        PagoDetalle detalle = new PagoDetalle();
        detalle.setMonto(new BigDecimal("45.00"));
        detalle.setObservaciones("Detalle actualizado");
        Response response = webTarget.path("pago")
                .path(pagoId.toString())
                .path("detalle")
                .path(detalleId.toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(detalle, MediaType.APPLICATION_JSON));
        assertEquals(200, response.getStatus());
        response = webTarget.path("pago")
                .path(pagoId.toString())
                .path("detalle")
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(200, response.getStatus());
        List<PagoDetalle> detalles = response.readEntity(new GenericType<List<PagoDetalle>>() {});
        assertNotNull(detalles);
        assertEquals(1, detalles.size());
        assertEquals(new BigDecimal("45.00"), detalles.get(0).getMonto());
    }
}