package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ProductoPrecioDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoPrecio;
import java.math.BigDecimal;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductoPrecioResourceSI extends AbstractContainerTest {

    @Test
    @Order(1)
    public void testCreate() {
        System.out.println("ProductoPrecioResource.Create");
        Long idProducto = 6L; //producto existe en la bd
        ProductoPrecio nuevoPrecio = new ProductoPrecio();
        nuevoPrecio.setPrecioSugerido(new BigDecimal("9.99"));
        nuevoPrecio.setFechaDesde(new Date());
        Response response = webTarget.path("producto")
                .path(idProducto.toString())
                .path("precio")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(nuevoPrecio, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        ProductoPrecio precioCreado = response.readEntity(ProductoPrecio.class);
        assertNotNull(precioCreado.getIdProductoPrecio());
        assertEquals(new BigDecimal("9.99"), precioCreado.getPrecioSugerido());
    }

    @Test
    @Order(1)
    public void findById() {
        System.out.println("ProductoPrecioResource.ObtenerPrecio");
        Long idProductoExistente = 1L;
        String precioSugerido = "5.99";

        Response response = webTarget.path("producto")
                .path(idProductoExistente.toString())
                .path("precio")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ProductoPrecioDTO precioDTO = response.readEntity(ProductoPrecioDTO.class);
        assertNotNull(precioDTO);
        assertEquals(precioSugerido, precioDTO.getPrecioSugerido().toString());
        assertNotNull(precioDTO.getIdProducto());
        assertNotNull(precioDTO.getNombreProducto());
    }

    @Test
    @Order(2)
    public void testUpdate() {
        System.out.println("ProductoPrecioResource.Update");

        Long idProducto = 1L; // Hamburguesa
        Long idPrecio = 1L;   // Precio de Hamburguesa
        ProductoPrecio updateData = new ProductoPrecio();
        updateData.setPrecioSugerido(new BigDecimal("6.99"));
        updateData.setFechaHasta(new Date());
        Response response = webTarget.path("producto")
                .path(idProducto.toString())
                .path("precio")
                .path(idPrecio.toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(updateData, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        ProductoPrecioDTO precioModificadoDTO = response.readEntity(ProductoPrecioDTO.class);
        assertNotNull(precioModificadoDTO);
        assertEquals(new BigDecimal("6.99"), precioModificadoDTO.getPrecioSugerido());
        assertEquals(idPrecio, precioModificadoDTO.getIdProductoPrecio());
        assertEquals(idProducto, precioModificadoDTO.getIdProducto());
        assertNotNull(precioModificadoDTO.getNombreProducto()); // Verificar que el DTO tiene nombre
    }

    @Test
    @Order(4)
    public void testDelete() {
        System.out.println("ProductoPrecioResource.Delete");
        Long idProducto = 7L;
        Long idPrecio = 53L;
        Response deleteResponse = webTarget.path("producto")
                .path(idProducto.toString())
                .path("precio")
                .path(idPrecio.toString())
                .request()
                .delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        Response getResponse = webTarget.path("producto")
                .path(idProducto.toString())
                .path("precio")
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), getResponse.getStatus());
    }
}