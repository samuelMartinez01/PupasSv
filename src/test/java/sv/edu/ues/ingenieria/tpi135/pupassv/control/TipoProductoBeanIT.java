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
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.TipoProducto;
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
public class TipoProductoBeanIT {

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
        HashMap<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url",
                String.format("jdbc:postgresql://localhost:%d/PupasBd_tpi2025",
                        postgres.getMappedPort(5432)));
        emf = Persistence.createEntityManagerFactory("PupaTest", propiedades);
    }

    @Test
    @Order(1)
    public void testCount() {
        System.out.println("TipoProductoBeanIT.testCount");
        TipoProductoBean cut = new TipoProductoBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Long esperado = 3L;
        Long resultado = cut.count();
        assertEquals(esperado, resultado);
    }

    @Test
    @Order(2)
    public void testInsert() {
        System.out.println("TipoProductoBeanIT.testInsert");
        TipoProductoBean cut = new TipoProductoBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        TipoProducto nuevo = new TipoProducto();
        nuevo.setNombre("BEBIDAS");
        nuevo.setActivo(true);

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            cut.create(nuevo);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            fail("Error en la inserción");
        }

        assertNotNull(nuevo.getIdTipoProducto());
        assertEquals(4L, cut.count());
    }

    @Test
    @Order(3)
    public void testFindById() {
        System.out.println("TipoProductoBeanIT.testFindById");
        TipoProductoBean cut = new TipoProductoBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        TipoProducto resultado = cut.findById(1);
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdTipoProducto());
    }

    @Test
    @Order(4)
    public void testFindRange() {
        System.out.println("TipoProductoBeanIT.testFindRange");
        TipoProductoBean cut = new TipoProductoBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        List<TipoProducto> resultados = cut.findRange(0, 2);
        assertNotNull(resultados);
        assertEquals(2, resultados.size());
    }

    @Test
    @Order(5)
    public void testUpdate() {
        System.out.println("TipoProductoBeanIT.testUpdate");
        TipoProductoBean cut = new TipoProductoBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        TipoProducto tipoProducto = cut.findById(1);
        tipoProducto.setNombre("MODIFICADO");
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TipoProducto actualizado = cut.update(tipoProducto);
            tx.commit();
            assertNotNull(actualizado);
            assertEquals("MODIFICADO", actualizado.getNombre());
        } catch (Exception ex) {
            tx.rollback();
            throw ex;
        }
    }

    @Test
    @Order(6)
    public void testDelete() {
        System.out.println("TipoProductoBeanIT.testDelete");
        TipoProductoBean cut = new TipoProductoBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;

        TipoProducto nuevo = new TipoProducto();
        nuevo.setNombre("TEMPORAL");
        nuevo.setActivo(true);

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            cut.create(nuevo);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }

        Integer idEliminar = nuevo.getIdTipoProducto();
        assertNotNull(idEliminar);

        try {
            tx.begin();
            cut.delete(idEliminar);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }

        TipoProducto eliminado = cut.findById(idEliminar);
        assertNull(eliminado);
    }

}
