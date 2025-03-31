package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;
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
        System.out.println("findAll");
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
        String formatoPath = String.format("tipoproducto/%d/producto", idTipoProducto);
        webTarget.path(formatoPath)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(p, MediaType.APPLICATION_JSON));
        //Verificar findAll
        response = webTarget.path("tipoproducto/all/producto")
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Producto> productos = response.readEntity(new GenericType<List<Producto>>() {
        });
        assertNotNull(productos);
        assertFalse(productos.isEmpty());
        Long totalRecords = Long.parseLong(response.getHeaderString(Headers.TOTAL_RECORD));
        assertTrue(totalRecords == 1);
    }


    @Test
    @Order(2)
    public void testCreate() {
        System.out.println("Create");
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
        Producto p = new Producto();
        p.setNombre("Coca Test");
        String formatoPath = String.format("tipoproducto/%d/producto", idTipoProducto);
        response = webTarget.path(formatoPath)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(p, MediaType.APPLICATION_JSON));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        String pLocation = response.getLocation().toString();
    }


    @Test
    @Order(3)
    public void testFindById() {
        System.out.println("FindById");
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
        String formatoPath = String.format("tipoproducto/%d/producto", idTipoProducto);
        response = webTarget.path(formatoPath)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(producto, MediaType.APPLICATION_JSON));

        String productoLocation = response.getLocation().toString();
        Integer idProducto = Integer.parseInt(
                productoLocation.substring(
                        productoLocation.lastIndexOf('/') + 1));

        response = webTarget.path(formatoPath).path(idProducto.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Producto productoObtenido = response.readEntity(Producto.class);
        assertEquals(idProducto.toString(), productoObtenido.getIdProducto().toString());
    }

    @Test
    @Order(4)
    public void testUpdate() {
        System.out.println("Update");
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
        String formatoPath = String.format("tipoproducto/%d/producto", idTipoProducto);
        response = webTarget.path(formatoPath)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(producto, MediaType.APPLICATION_JSON)); //POST
        //se obtiene su ubicacion y su id
        String location = response.getLocation().toString();
        Integer id = Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
        //Se hace una peticion para traerc el obj ya creado
        response = webTarget.path(formatoPath).path(id.toString())
                .request(MediaType.APPLICATION_JSON).get(); //GET
        //Se procede a actualizar
        Producto pActual = response.readEntity(Producto.class);
        pActual.setNombre("actualizado");
        response = webTarget.path(formatoPath)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(pActual, MediaType.APPLICATION_JSON)); //PUT
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        //Verifica la actualizacion haciendo una nueva peticion
        response = webTarget.path(formatoPath).path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        Producto pActualizado = response.readEntity(Producto.class);
        assertNotNull(pActualizado);
        assertEquals("actualizado", pActualizado.getNombre());
    }

//    @Test
//    @Order(5)
//    public void testDelete() {
//        System.out.println("Delete");
//        //Crea un tipo para el producto a crear
//        TipoProducto tipoProducto = new TipoProducto();
//        tipoProducto.setNombre("New tipo");
//        Response response = webTarget.path("tipoproducto")
//                .request(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(tipoProducto, MediaType.APPLICATION_JSON));
//        Integer idTipoProducto = Integer.parseInt(response.getLocation().toString()
//                .substring(response.getLocation().toString()
//                        .lastIndexOf('/') + 1));
//        System.out.println(idTipoProducto);
//        String formatoPath = String.format("tipoproducto/%d/producto", idTipoProducto);
//        //Se crea el producto basandose en el tipo creado
//        Producto producto = new Producto();
//        producto.setNombre("Coca Test");
//        response = webTarget.path(formatoPath)
//                .request(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(producto, MediaType.APPLICATION_JSON)); //POST
//        //se obtiene su ubicacion y su id
//        String location = response.getLocation().toString();
//        Integer id = Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
//        System.out.println("id a eliminar: " +id);
//        // Eliminamos el producto
//        response = webTarget.path(formatoPath).path(id.toString())
//                .request(MediaType.APPLICATION_JSON)
//                .delete();
//
//        // Verificar que la eliminación fue exitosa
//        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
//
//        // Intentamos encontrar el recurso eliminado
//        response = webTarget.path("tipoproducto").path(id.toString())
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//
//        // Verificar que el recurso ya no existe
//        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
//    }

}