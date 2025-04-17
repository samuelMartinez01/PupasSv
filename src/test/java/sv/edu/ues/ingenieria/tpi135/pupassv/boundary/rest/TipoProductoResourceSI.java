package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.TipoProducto;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TipoProductoResourceSI  extends AbstractContainerTest {

    @Test
    @Order(1)
    public void testFindRange() {
        System.out.println("TipoProductoResource.findRange");
        Response response = webTarget.path("tipoproducto")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<TipoProducto> tiposProducto = response.readEntity(new GenericType<List<TipoProducto>>() {});
        assertNotNull(tiposProducto);
        assertFalse(tiposProducto.isEmpty());
        Long totalRecords = Long.parseLong(response.getHeaderString(Headers.TOTAL_RECORD));
        assertTrue(totalRecords == 3);
    }

    @Test
    @Order(2)
    public void testCreate() {
        System.out.println("TipoProductoResource.Create");
        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setNombre("New tipo");
        tipoProducto.setActivo(true);
        tipoProducto.setObservaciones("test");
        // Se crea el registro
        Response response = webTarget.path("tipoproducto")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(tipoProducto, MediaType.APPLICATION_JSON));
        // comprueba si se creo
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getLocation());
        // Obtinene la hubicacion del nuevo aobjeto creado
        String location = response.getLocation().toString();
        Integer id = Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
        assertEquals(4, id);
    }

    @Test
    @Order(3)
    public void testFindById() {
        System.out.println("TipoProductoResource.FindById");
        Long id = 3L;
        Response findResponse = webTarget.path("tipoproducto").path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), findResponse.getStatus());
        TipoProducto tipoEncontrado = findResponse.readEntity(TipoProducto.class);
        assertNotNull(tipoEncontrado);
        assertEquals(id, tipoEncontrado.getIdTipoProducto().longValue());
        assertEquals("tipicos", tipoEncontrado.getNombre());

        // busqueda para tipo que no existe
        findResponse = webTarget.path("tipoproducto/999999")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(404, findResponse.getStatus());
    }

    @Test
    @Order(4)
    public void testUpdate() {
        System.out.println("TipoProductoResource.Update");
        //Se crea un objeto de tipo producto
        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setNombre("Tipo actual");
        tipoProducto.setActivo(true);
        tipoProducto.setObservaciones("test actual");
        Response createResponse = webTarget.path("tipoproducto")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(tipoProducto, MediaType.APPLICATION_JSON));
        // Obtiene la hubicacion del objeto creado y su id para comprobar su efectividad
        String location = createResponse.getLocation().toString();
        Integer id = Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
        Response getResponse = webTarget.path("tipoproducto")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        TipoProducto objActual = getResponse.readEntity(TipoProducto.class);

        //Proceso de actualizacion del objeto
        objActual.setNombre("Tipo actualizado");
        Response updateResponse = webTarget.path("tipoproducto")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(objActual, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.OK.getStatusCode(), updateResponse.getStatus());
        //Verifica la actualizacion
        Response getUpdatedResponse = webTarget.path("tipoproducto")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        TipoProducto tipoActualizado = getUpdatedResponse.readEntity(TipoProducto.class);
        assertNotNull(tipoActualizado);
        assertEquals("Tipo actualizado", tipoActualizado.getNombre());
    }

    @Test
    @Order(5)
    public void testDelete() {
        System.out.println("TipoProductoResource.Delete");
        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setNombre("Tipo a eliminar");
        tipoProducto.setActivo(true);
        tipoProducto.setObservaciones("Este será eliminado");

        Response createResponse = webTarget.path("tipoproducto")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(tipoProducto, MediaType.APPLICATION_JSON));

        String location = createResponse.getLocation().toString();
        Integer id = Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));

        // Eliminar
        Response deleteResponse = webTarget.path("tipoproducto")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();

        assertEquals(Response.Status.OK.getStatusCode(), deleteResponse.getStatus());

        Response getResponse = webTarget.path("tipoproducto")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), getResponse.getStatus());

        // Eliminar un registro que no existe
        Response deleteNonExistentResponse = webTarget.path("tipoproducto")
                .path("999999")
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(422, deleteNonExistentResponse.getStatus());
    }

}
