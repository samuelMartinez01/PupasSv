package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ProductoDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductoResourceSI extends AbstractContainerTest {
    @Test
    @Order(1)
    public void testFinRange() {
        System.out.println("ProductoResource.fidRange");
        //Verificar findAll
        Response response = webTarget.path("producto")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<ProductoDTO> productos = response.readEntity(new GenericType<List<ProductoDTO>>() {
        });
        assertNotNull(productos);
        assertFalse(productos.isEmpty());
    }


    @Test
    @Order(2)
    public void testCreate() {
        System.out.println("ProductoResource.Create");
        Producto producto = new Producto();
        producto.setNombre("Coca Test");
        producto.setObservaciones("Observaciones");
        producto.setActivo(true);
        String path = String.format("producto");
        Response response = webTarget.path(path)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(producto, MediaType.APPLICATION_JSON));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        String pLocation = response.getLocation().toString();
        assertNotNull(pLocation);
        //Se obtiene él, id del producto creado
        Integer idProducto = Integer.parseInt( pLocation.substring(
                pLocation.lastIndexOf('/') + 1));
        response = webTarget.path(path).path(String.valueOf(idProducto))
                .request(MediaType.APPLICATION_JSON)
                .get();
        ProductoDTO productoDTO = response.readEntity(ProductoDTO.class);
        assertNotNull(productoDTO.getIdProducto());
        assertEquals("Coca Test", productoDTO.getNombre());
        assertEquals("Observaciones", productoDTO.getObservaciones());
        assertTrue(productoDTO.getActivo());
        assertNotNull(productoDTO.getTipo());

    }


    @Test
    @Order(3)
    public void testFindById() {
        System.out.println("ProductoResource.FindById");
        Long idProducto = 1L;
        Response response = webTarget.path("producto").path(String.valueOf(idProducto))
                .request(MediaType.APPLICATION_JSON)
                .get();
        //leer como DTO
        ProductoDTO productoDTO = response.readEntity(ProductoDTO.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(idProducto.longValue(), productoDTO.getIdProducto());
        assertEquals("Hamburguesa", productoDTO.getNombre());
        assertEquals(idProducto.toString(), productoDTO.getIdProducto().toString());
    }

    @Test
    @Order(4)
    public void testUpdate() {
        System.out.println("ProductoResource.Update");
        Long idProducto = 5L;
    Response    response = webTarget.path("producto")
                .path(idProducto.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(); //GET
        //Actualizacion de Datos
       ProductoDTO productoDTO = response.readEntity(ProductoDTO.class);
        assertEquals(productoDTO.getNombre(), "Cerveza");
        productoDTO.setNombre("Coca Actualizado");
        productoDTO.setActivo(false);
        response = webTarget.path("producto").
                request(MediaType.APPLICATION_JSON).
                put(Entity.entity(productoDTO, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        //Verifica la actualizacion haciendo una nueva peticion
        response = webTarget.path("producto").path(idProducto.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        ProductoDTO pActualizado = response.readEntity(ProductoDTO.class);
        assertNotNull(pActualizado);
        assertEquals("Coca Actualizado", pActualizado.getNombre());
        assertFalse(pActualizado.getActivo());
        response = webTarget.path("producto").path("222434")
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(404, response.getStatus());
    }

    @Test
    @Order(5)
    public void testFindPorTipo() {
        System.out.println("ProductoResource.findPorTipo");
        Long idTipoProducto = 1L;
        Response response = webTarget.path("producto/tipoproducto").path(String.valueOf(idTipoProducto))
                .request(MediaType.APPLICATION_JSON)
                .get(); //GET
        assertEquals(200, response.getStatus());
        List<ProductoDTO> productos = response.readEntity(new GenericType<List<ProductoDTO>>() {});
        assertNotNull(productos);
        assertEquals(2, productos.size());
        // parametros invalidos
        response = webTarget.path("producto/tipoproducto").path(String.valueOf(idTipoProducto))
                .queryParam("first", -1)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(400, response.getStatus());
        // Buscar un tipo que no existe
        response = webTarget.path("producto/tipoproducto/999999")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(404, response.getStatus());
    }
    @Test
    @Order(6)
    public void testDelete() {
        System.out.println("ProductoResource.Delete");
        Long idProducto =2L;
        Response response = webTarget.path("producto")
                .path(idProducto.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(200, response.getStatus());
        //Eliminacion
         response = webTarget.path("producto")
                .path(idProducto.toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        // Verificar que ya no existe
        response = webTarget.path("producto")
                .path(idProducto.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(404, response.getStatus());
    }

}