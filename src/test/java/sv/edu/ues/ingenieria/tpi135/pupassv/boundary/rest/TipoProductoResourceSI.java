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
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Orden;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.TipoProducto;

import java.net.URI;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TipoProductoResourceSI  extends AbstractContainerTest {

    @Test
    @Order(1)
    public void testFindRange() {
        System.out.println("findRange");
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
        System.out.println("Create");
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
        System.out.println("FindById");
        Long id = 3L;
        Response findResponse = webTarget.path("tipoproducto").path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), findResponse.getStatus());
        TipoProducto tipoEncontrado = findResponse.readEntity(TipoProducto.class);
        assertNotNull(tipoEncontrado);
        assertEquals(id, tipoEncontrado.getIdTipoProducto().longValue());
        assertEquals("tipicos", tipoEncontrado.getNombre());

        // 7. Probar búsqueda para tipo que no existe
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
        System.out.println("Update");
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

//    @Test
//    @Order(5)
//    public void testDelete() {
//        System.out.println("Delete");
//
//        // Se crea un objeto de tipo producto
//        TipoProducto tipoProducto = new TipoProducto();
//        tipoProducto.setNombre("Tipo actual");
//        tipoProducto.setActivo(true);
//        tipoProducto.setObservaciones("test actual");
//
//        WebTarget target = webTarget.path("tipoproducto");
//        Response createResponse = target
//                .request(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(tipoProducto, MediaType.APPLICATION_JSON));
//
//        // Verificar que la creación fue exitosa
//        assertEquals(Response.Status.CREATED.getStatusCode(), createResponse.getStatus());
//
//        // Obtener la ubicación del objeto creado
//        URI location = createResponse.getLocation();
//        assertNotNull(location, "La ubicación del recurso no debería ser nula.");
//
//        String id = location.getPath().substring(location.getPath().lastIndexOf('/') + 1);
//
//        // Eliminamos el tipo
//        WebTarget deleteTarget = webTarget.path("tipoproducto").path(id);
//        Response deleteResponse = deleteTarget
//                .request(MediaType.APPLICATION_JSON)
//                .delete();
//
//        // Verificar que la eliminación fue exitosa
//        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
//
//        // Intentamos encontrar el recurso eliminado
//        WebTarget findTarget = webTarget.path("tipoproducto").path(id);
//        Response findResponse = findTarget
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//
//        // Verificar que el recurso ya no existe
//        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), findResponse.getStatus());
//    }

}
