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
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.TipoProducto;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductoResourceSI extends AbstractContainerTest {
    @Test
    @Order(1)
    public void testFindAll() {
        System.out.println("ProductoResource.findAll");
        //Crear un tipo y un producto
        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setNombre("New tipo");
        Response response = webTarget.path("tipoproducto")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(tipoProducto, MediaType.APPLICATION_JSON));
        Integer idTipoProducto = Integer.parseInt(response.getLocation().toString()
                .substring(response.getLocation().toString()
                        .lastIndexOf('/') + 1));
        Producto p = new Producto();
        p.setNombre("Coca Test");
        String path = String.format("tipoproducto/%d/producto", idTipoProducto);
        webTarget.path(path)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(p, MediaType.APPLICATION_JSON));
        //Verificar findAll
        response = webTarget.path("tipoproducto/all/producto")
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
        //Se crea un tipo para el producto a crear
        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setNombre("New tipo");
        Response response = webTarget.path("tipoproducto")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(tipoProducto, MediaType.APPLICATION_JSON));
        Integer idTipoProducto = Integer.parseInt(response.getLocation().toString()
                .substring(response.getLocation().toString()
                        .lastIndexOf('/') + 1));
        //Se crea el producto basandose en el tipo creado
        Producto p = new Producto();
        p.setNombre("Coca Test");
        p.setObservaciones("Observaciones");
        p.setActivo(true);
        String path = String.format("tipoproducto/%d/producto", idTipoProducto);
        response = webTarget.path(path)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(p, MediaType.APPLICATION_JSON));
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

        //Se crea un tipo para el producto a crear
        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setNombre("New tipo");
        Response response = webTarget.path("tipoproducto")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(tipoProducto, MediaType.APPLICATION_JSON));
        Integer idTipoProducto = Integer.parseInt(response.getLocation().toString()
                .substring(response.getLocation().toString()
                        .lastIndexOf('/') + 1));

        //Se crea el producto basandose en el tipo creado
        Producto producto = new Producto();
        producto.setNombre("Coca Test");
        String path = String.format("tipoproducto/%d/producto", idTipoProducto);
        response = webTarget.path(path)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(producto, MediaType.APPLICATION_JSON));

        //Se obtiene él, id del producto creado
        String productoLocation = response.getLocation().toString();
        Integer idProducto = Integer.parseInt( productoLocation.substring(
                productoLocation.lastIndexOf('/') + 1));

        response = webTarget.path(path).path(String.valueOf(idProducto))
                .request(MediaType.APPLICATION_JSON)
                .get();

        //leer como DTO
        ProductoDTO productoDTO = response.readEntity(ProductoDTO.class);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(idProducto.longValue(), productoDTO.getIdProducto());
        assertEquals("Coca Test", productoDTO.getNombre());
        assertEquals(idProducto.toString(), productoDTO.getIdProducto().toString());
    }

    @Test
    @Order(4)
    public void testUpdate() {
        System.out.println("ProductoResource.Update");
        //Se crea un tipo
        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setNombre("New tipo");
        Response response = webTarget.path("tipoproducto")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(tipoProducto, MediaType.APPLICATION_JSON));
        Integer idTipoProducto = Integer.parseInt(response.getLocation().toString()
                .substring(response.getLocation().toString()
                        .lastIndexOf('/') + 1));
        //Se crea el producto basandose en el tipo creado
        Producto producto = new Producto();
        producto.setNombre("Coca Test");

        String path = String.format("tipoproducto/%d/producto", idTipoProducto);

        response = webTarget.path(path)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(producto, MediaType.APPLICATION_JSON)); //POST
        //se obtiene su ubicacion y su idProducto
        String location = response.getLocation().toString();
        Integer idProducto = Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
        //Se hace una peticion para traerc el obj ya creado
        response = webTarget.path(path)
                .path(idProducto.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(); //GET
        //Actualizacion de Datos
        ProductoDTO productoDTO = response.readEntity(ProductoDTO.class);
        productoDTO.setNombre("Coca Actualizado");
        productoDTO.setActivo(false);
        response = webTarget.path(path).
                request(MediaType.APPLICATION_JSON).
                put(Entity.entity(productoDTO, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        //Verifica la actualizacion haciendo una nueva peticion
        response = webTarget.path(path).path(idProducto.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        ProductoDTO pActualizado = response.readEntity(ProductoDTO.class);
        assertNotNull(pActualizado);
        assertEquals("Coca Actualizado", pActualizado.getNombre());
        assertFalse(pActualizado.getActivo());
        response = webTarget.path(path).path("222434")
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(404, response.getStatus());
    }

    @Test
    @Order(5)
    public void testFindPorTipo() {
        System.out.println("ProductoResource.findPorTipo");
        //Crea un tipo para el producto a crear
        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setNombre("New tipo");
        Response response = webTarget.path("tipoproducto")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(tipoProducto, MediaType.APPLICATION_JSON));
        Integer idTipoProducto = Integer.parseInt(response.getLocation().toString()
                .substring(response.getLocation().toString()
                        .lastIndexOf('/') + 1));
        //Se crea el producto basandose en el tipo creado
        Producto producto = new Producto();
        producto.setNombre("Coca Test");
        String path = String.format("tipoproducto/%d/producto", idTipoProducto);
        response = webTarget.path(path)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(producto, MediaType.APPLICATION_JSON)); //POST
        Producto productoDos = new Producto();
        productoDos.setNombre("Coca TestDos");
        response = webTarget.path(path)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(producto, MediaType.APPLICATION_JSON)); //POST
        //se obtiene su ubicacion y su id
        String location = response.getLocation().toString();
        Integer id = Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
        //Se hace una peticion para traerc el obj ya creado
        response = webTarget.path(path)
                .request(MediaType.APPLICATION_JSON)
                .get(); //GET
        assertEquals(200, response.getStatus());
        List<ProductoDTO> productos = response.readEntity(new GenericType<List<ProductoDTO>>() {});
        assertNotNull(productos);
        assertEquals(2, productos.size());
        Long totalRecords = Long.parseLong(response.getHeaderString(Headers.TOTAL_RECORD));
        assertTrue(totalRecords >= 2);
        // búsqueda de todos los productos con all
        response = webTarget.path("tipoproducto/all/producto")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(200, response.getStatus());
        productos = response.readEntity(new GenericType<List<ProductoDTO>>() {});
        assertNotNull(productos);
        assertFalse(productos.isEmpty());
        // parametros invalidos
        response = webTarget.path(path)
                .queryParam("first", -1)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(400, response.getStatus());
        // Buscar un tipo que no existe
        response = webTarget.path("tipoproducto/999999/producto")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(404, response.getStatus());
    }
    @Test
    @Order(5)
    public void testDelete() {
        System.out.println("ProductoResource.Delete");
        // Crear tipo producto
        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setNombre("New tipo");
        Response response = webTarget.path("tipoproducto")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(tipoProducto, MediaType.APPLICATION_JSON));
        Integer idTipoProducto = Integer.parseInt(response.getLocation().toString()
                .substring(response.getLocation().toString().lastIndexOf('/') + 1));
        // Crear producto
        Producto p = new Producto();
        p.setNombre("Coca Test");
        String path = String.format("tipoproducto/%d/producto", idTipoProducto);
        response = webTarget.path(path)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(p, MediaType.APPLICATION_JSON));
        String pLocation = response.getLocation().toString();
        Long idProducto = Long.parseLong(pLocation.substring(pLocation.lastIndexOf('/') + 1));

        //Eliminacion
        response = webTarget.path(path)
                .path(idProducto.toString())
                .queryParam("idTipoProducto", idTipoProducto)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        // Verificar que ya no existe
        response = webTarget.path(path)
                .path(idProducto.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(404, response.getStatus());
    }

}