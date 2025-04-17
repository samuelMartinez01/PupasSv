package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ComboDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ProductoComboDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Combo;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ComboResourceSI  extends AbstractContainerTest {

    @Test
    @Order(1)
    public void testCreate() {
        System.out.println("ComboResource.Create");
        Combo combo = new Combo();
        combo.setIdCombo(5L);
        combo.setNombre("New combo");
        combo.setActivo(true);
        combo.setDescripcionPublica("test");
        Response response = webTarget.path("combo")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(combo, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getLocation());
        String location = response.getLocation().toString();
        Long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
        assertEquals(5L, id);
    }


    @Test
    @Order(2)
    public void testFindRange() {
        System.out.println("ComboResource.findRange");
        Response response = webTarget.path("combo")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<ComboDTO> combos = response.readEntity(new GenericType<List<ComboDTO>>() {});
        assertNotNull(combos);
        assertFalse(combos.isEmpty());
        Long totalRecords = Long.parseLong(response.getHeaderString(Headers.TOTAL_RECORD));
        assertTrue(totalRecords == 2); //2 combos insertados en el script de la bd
    }

    @Test
    @Order(3)
    public void testFindById() {
        System.out.println("ComboResource.FindById");
        Long id = 2L;
        Response response = webTarget.path("combo").path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ComboDTO comboEncontrado = response.readEntity(ComboDTO.class);
        assertNotNull(comboEncontrado);
        assertEquals(id, comboEncontrado.getIdCombo().longValue());
        assertEquals("Combo Infantil", comboEncontrado.getNombre());

        // Combo que no existe
        response = webTarget.path("ComboDTO/456")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(404, response.getStatus());
    }

    @Test
    @Order(4)
    public void testUpdate() {
        System.out.println("ComboResource.Update");
        Long id = 2L;
        Response response = webTarget.path("combo")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Combo comboActual = response.readEntity(Combo.class);
        comboActual.setNombre("Combo Infantil Grande");
        response = webTarget.path("combo")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(comboActual, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = webTarget.path("combo")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        Combo comboActualizado = response.readEntity(Combo.class);
        assertNotNull(comboActualizado);
        assertEquals("Combo Infantil Grande", comboActualizado.getNombre());
    }

    @Test
    @Order(5)
    public void testDelete() {
        System.out.println("ComboResource.Delete");
        Integer idCombo = 1;
        Response response = webTarget.path("combo")
                .path(idCombo.toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        response = webTarget.path("combo")
                .path(idCombo.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(404, response.getStatus());
    }

    @Test
    @Order(6)
    public void asignarProductosACombo() {
        System.out.println("ComboResource.AsignarProductosACombo");
        Integer idComboAsignar = 2; //Familiar
        ProductoComboDTO productoDTO = new ProductoComboDTO();
        productoDTO.setIdProducto(5L); // producto a asignar
        productoDTO.setCantidad(2);    // cantidad
        List<ProductoComboDTO> productos = List.of(productoDTO);
        Response response = webTarget.path("combo")
                .path(idComboAsignar.toString())
                .path("productos")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(productos, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        //Peticion y comprobacion
        response = webTarget.path("combo")
                .path(idComboAsignar.toString())
                .path("productos")
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<ProductoComboDTO> productosAsignados = response.readEntity(new GenericType<List<ProductoComboDTO>>() {});

        boolean productoAsignado = productosAsignados.stream()
                .anyMatch(p -> p.getIdProducto().equals(5L));
        assertTrue(productoAsignado);
    }

    @Test
    @Order(7)
    public void eliminarProductosDeCombo() {
        System.out.println("ComboResource.eliminarProductosDeCombo");
        Long idCombo = 2L; //infantil
        Long idProducto = 5L; //cerveza
        Response response = webTarget.path("combo")
                .path(idCombo.toString())
                .path("productos")
                .queryParam("idProducto", idProducto)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = webTarget.path("combo")
                .path(idCombo.toString())
                .path("productos")
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<ProductoComboDTO> productosAsignados = response.readEntity(new GenericType<List<ProductoComboDTO>>() {});

        boolean productoAsignado = productosAsignados.stream()
                .anyMatch(p -> p.getIdProducto().equals(5L));
        assertFalse(productoAsignado);
    }
}