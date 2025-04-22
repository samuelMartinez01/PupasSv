package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.OrdenDetalle;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.OrdenDetallePK;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdenDetalleBeanIT {

    EntityManagerFactory emf;

    static Network red = Network.newNetwork();

    @Container
    static GenericContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("PupasBd_tpi2025")
            .withUsername("postgres")
            .withPassword("abc123")
            .withInitScript("pupas_ddl.sql")
            .withExposedPorts(5432)
            .withNetworkAliases("db");

    @BeforeAll
    public void init() {
        HashMap<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url",
                String.format("jdbc:postgresql://localhost:%d/PupasBd_tpi2025", postgres.getMappedPort(5432)));
        emf = Persistence.createEntityManagerFactory("PupaTest", props);
    }

    @Test
    @Order(1)
    public void testInsert() {
        System.out.println("OrdenDetalleBeanIT.testInsert");
        OrdenDetalleBean cut = new OrdenDetalleBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        OrdenDetallePK pk = new OrdenDetallePK(1431L, 53L);
        OrdenDetalle nuevo = new OrdenDetalle(pk);
        nuevo.setCantidad(5);
        nuevo.setPrecio(BigDecimal.valueOf(5));
        nuevo.setObservaciones("Detalle de prueba");
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            cut.create(nuevo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            fail("Error al insertar OrdenDetalle: " + e.getMessage());
        }
        OrdenDetalle encontrado = cut.findByPk(pk);
        assertNotNull(encontrado);
        assertEquals("Detalle de prueba", encontrado.getObservaciones());
    }

    @Test
    @Order(2)
    public void testFindByPk() {
        System.out.println("OrdenDetalleBeanIT.testFindByPk");
        OrdenDetalleBean cut = new OrdenDetalleBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        OrdenDetallePK pk = new OrdenDetallePK(1433L, 53L);
        OrdenDetalle nuevo = new OrdenDetalle(pk);
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            cut.create(nuevo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            fail("Error al insertar OrdenDetalle: " + e.getMessage());
        }
        //Buscar
        OrdenDetalle encontrado = cut.findByPk(pk);
        assertNotNull(encontrado);
    }

    @Test
    @Order(3)
    public void testUpdate() {
        System.out.println("OrdenDetalleBeanIT.testUpdate");
        OrdenDetalleBean cut = new OrdenDetalleBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        OrdenDetallePK pk = new OrdenDetallePK(1432L, 53L);
        OrdenDetalle nuevo = new OrdenDetalle(pk);
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            cut.create(nuevo);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            fail("Error al insertar OrdenDetalle: " + e.getMessage());
        }
        //Buscar primary key
        OrdenDetalle detalle = cut.findByPk(pk);
        assertNotNull(detalle);
        detalle.setObservaciones("Modificado desde IT");
        try {
            tx.begin();
            cut.update(detalle);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            fail("Error al actualizar OrdenDetalle");
        }

        OrdenDetalle actualizado = cut.findByPk(pk);
        assertEquals("Modificado desde IT", actualizado.getObservaciones());
    }

    @Test
    @Order(4)
    public void testFindRange() {
        System.out.println("OrdenDetalleBeanIT.testFindRange");
        OrdenDetalleBean cut = new OrdenDetalleBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        List<OrdenDetalle> detalles = cut.findRange(0, 10).stream()
                .filter(od -> od.getOrdenDetallePK().getIdOrden() == 1431L)
                .collect(Collectors.toList());
        assertFalse(detalles.isEmpty());
    }

    @Test
    @Order(5)
    public void testEliminarProductoDeOrden() {
        System.out.println("OrdenDetalleBeanIT.testEliminarProductoDeOrden");
        OrdenDetalleBean cut = new OrdenDetalleBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        OrdenDetallePK pk = new OrdenDetallePK(99L, 1L);
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            cut.eliminarProductoDeOrden(pk);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            fail("No se pudo eliminar producto de orden");
        }

        OrdenDetalle eliminado = cut.findByPk(pk);
        assertNull(eliminado);
    }

    @Test
    @Order(6)
    public void testEliminarRelacionOrdenDetalle() {
        System.out.println("OrdenDetalleBeanIT.testEliminarRelacionOrdenDetalle");
        OrdenDetalleBean cut = new OrdenDetalleBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        OrdenDetallePK pk1 = new OrdenDetallePK(1434L, 3L);
        OrdenDetallePK pk2 = new OrdenDetallePK(1433L, 2L);

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            cut.create(new OrdenDetalle(pk1));
            cut.create(new OrdenDetalle(pk2));
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }

        try {
            tx.begin();
            cut.eliminarRelacionOrdenDetalle(99L);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            fail("Error al eliminar relacion de detalles de orden");
        }

        List<OrdenDetalle> restantes = cut.findRange(0, 20);
        boolean existen = restantes.stream()
                .anyMatch(od -> od.getOrdenDetallePK().getIdOrden() == 99L);
        assertFalse(existen);
    }
}
