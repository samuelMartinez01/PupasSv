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
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoDetalle;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoDetallePK;
import java.util.HashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author wolf
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoDetalleBeanIT {

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
    public void testCount() {
        System.out.println("ProductoDetalleBeanIT.testCount");
        ProductoDetalleBean cut = new ProductoDetalleBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        ProductoDetallePK pk = new ProductoDetallePK(1, 1L);
        ProductoDetalle nuevo = new ProductoDetalle(pk);
        nuevo.setActivo(true);
        nuevo.setObservaciones("Prueba para contar");
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            cut.create(nuevo);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            fail("Error al insertar ProductoDetalle");
        }
        Long esperado = 3L;
        Long resultado = cut.count();
        assertEquals(esperado, resultado);
    }


    @Test
    @Order(2)
    public void testInsert() {
        System.out.println("ProductoDetalleIT.testInsert");
        ProductoDetalleBean cut = new ProductoDetalleBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        ProductoDetallePK pk = new ProductoDetallePK(1, 2L); // asegúrate que estos IDs existen
        ProductoDetalle nuevo = new ProductoDetalle(pk);
        nuevo.setActivo(true);
        nuevo.setObservaciones("Prueba IT");
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            cut.create(nuevo);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            fail("Error al insertar ProductoDetalle");
        }
        ProductoDetalle encontrado = cut.findById(pk);
        assertNotNull(encontrado);
        assertEquals("Prueba IT", encontrado.getObservaciones());
    }

    @Test
    @Order(3)
    public void testFindById() {
        System.out.println("ProductoDetalleIT.testFindById");
        ProductoDetalleBean cut = new ProductoDetalleBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        ProductoDetallePK pk = new ProductoDetallePK(1, 2L);
        ProductoDetalle encontrado = cut.findById(pk);
        assertNotNull(encontrado);
    }

    @Test
    @Order(4)
    public void testFindRange() {
        System.out.println("ProductoDetalleIT.testFindRange");
        ProductoDetalleBean cut = new ProductoDetalleBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        List<ProductoDetalle> lista = cut.findRange(0, 2);
        assertNotNull(lista);
        assertEquals(2, lista.size());
    }

    @Test
    @Order(5)
    public void testUpdate() {
        System.out.println("ProductoDetalleIT.testUpdate");
        ProductoDetalleBean cut = new ProductoDetalleBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        ProductoDetallePK pk = new ProductoDetallePK(1, 2L);
        ProductoDetalle detalle = cut.findById(pk);
        detalle.setObservaciones("Modificado IT");
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ProductoDetalle actualizado = cut.update(detalle);
            tx.commit();
            assertEquals("Modificado IT", actualizado.getObservaciones());
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }
    @Test
    @Order(6)
    public void testDelete() {
        System.out.println("ProductoDetalleIT.testDelete");
        ProductoDetalleBean cut = new ProductoDetalleBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        ProductoDetallePK pk = new ProductoDetallePK(2, 2L);
        ProductoDetalle nuevo = new ProductoDetalle(pk);
        nuevo.setActivo(true);
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            cut.create(nuevo);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
        assertNotNull(cut.findById(pk));
        try {
            tx.begin();
            cut.deletePk(pk);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
        ProductoDetalle eliminado = cut.findById(pk);
        assertNull(eliminado);
    }
}
