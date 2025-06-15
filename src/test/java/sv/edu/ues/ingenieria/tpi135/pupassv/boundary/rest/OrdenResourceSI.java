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
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.OrdenDetalleDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ProductoDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Orden;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.OrdenDetalle;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Date;
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
    @Order(1)
    public void testFinRange() {
        System.out.println("OrdenResource.fidRange");
        Response response = webTarget.path("orden")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        List<OrdenDTO> ordenes = response.readEntity(new GenericType<List<OrdenDTO>>() {});
        assertNotNull(ordenes);
        assertFalse(ordenes.isEmpty());
    }

    @Test
    @Order(2)
    public void findById() {
        System.out.println("OrdenResource.findById");
        Long id = 1432L;
        Response response = webTarget.path("orden").path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(200, response.getStatus());
        OrdenDTO ordenDTO = response.readEntity(OrdenDTO.class);
        assertNotNull(ordenDTO);
        assertEquals(id, ordenDTO.getIdOrden());
    }

    @Test
    @Order(3)
    public void create() {
        System.out.println("OrdenResource.Create");
        try {
            Orden orden = new Orden();
            orden.setFecha(new Date());
            orden.setSucursal("Sucu");
            orden.setAnulada(false);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error durante la creación de orden: " + e.getMessage());
        }
    }

    @Test
    public void update() {
        System.out.println("OrdenResource.Update");
        Long id = 1432L;
        OrdenDTO ordenActualizada = new OrdenDTO();
        ordenActualizada.setIdOrden(id);
        ordenActualizada.setSucursal("Zarza");
        ordenActualizada.setAnulada(true);
        Response response = webTarget.path("orden").path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(ordenActualizada, MediaType.APPLICATION_JSON));
        assertEquals(200, response.getStatus());
        OrdenDTO ordenDTO = response.readEntity(OrdenDTO.class);
        assertEquals("Zarza", ordenDTO.getSucursal());
        assertTrue(ordenDTO.getAnulada());
    }

    @Test
    public void delete() {
        System.out.println("OrdenResource.Delete");
        Long id = 1431L;
        Response response = webTarget.path("orden")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(204, response.getStatus());
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        response = webTarget.path("producto")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(404, response.getStatus());
    }

    @Test
    @Order(5)
    public void testDeleteProducto() {
        System.out.println("OrdenResource.deleteProducto");
        Long orderId = 1434L;
        Response deleteResponse = webTarget.path("orden")
                .path(orderId.toString())
                .path("productos")
                .path("3") // ID del producto a eliminar
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(204, deleteResponse.getStatus());
        Response notFoundOrderResponse = webTarget.path("orden")
                .path("999999") // ID de orden que no existe
                .path("productos")
                .path("1")
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(404, notFoundOrderResponse.getStatus());
        Response notFoundProductResponse = webTarget.path("orden")
                .path(orderId.toString())
                .path("productos")
                .path("999") // ID de producto que no existe en la orden
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(404, notFoundProductResponse.getStatus());
    }
}
