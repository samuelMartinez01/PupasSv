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
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;
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
public class ProductoBeanIT {

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
    public void testFindByIdTipoProducto() {
        System.out.println("ProductoBeanIT.testFindByIdTipoProducto");
        ProductoBean cut = new ProductoBean();
        EntityManager em = emf.createEntityManager();
        cut.setEm(em);
        List<Producto> lista = cut.findByIdTipoProducto(1, 0, 10);
        assertNotNull(lista);
        assertTrue(lista.size() == 2);
    }

    @Test
    @Order(2)
    public void testCountByIdTipoProducto() {
        System.out.println("ProductoBeanIT.testCountByIdTipoProducto");
        ProductoBean cut = new ProductoBean();
        EntityManager em = emf.createEntityManager();
        cut.setEm(em);
        Long cantidad = cut.countByIdTipoProducto(1, 0, 10);
        assertNotNull(cantidad);
        assertTrue(cantidad == 2);

    }
/**
    @Test
    @Order(3)
    public void testDeleteRelacion() {
        System.out.println("Eliminar relación y producto");

        ProductoBean cut = new ProductoBean();
        EntityManager em = emf.createEntityManager();
        cut.setEm(em);

        EntityTransaction tx = em.getTransaction();
        Long idProducto;
        Integer idTipoProducto = 2;

        try {
            tx.begin();
            // Insertar producto
            Producto p = new Producto();
            p.setNombre("Producto Dummy");
            p.setActivo(true);
            em.persist(p);
            em.flush(); // Asegura que se genere el ID y podamos usarlo

            idProducto = p.getIdProducto();

            // Insertar relación en producto_detalle
            em.createNativeQuery("INSERT INTO producto_detalle (id_tipo_producto, id_producto, activo) VALUES (?,?,true)")
                    .setParameter(1, idTipoProducto) // Corrige el orden de parámetros
                    .setParameter(2, idProducto) // Corrige el orden de parámetros
                    .executeUpdate();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            fail("Error durante inserción de prueba: " + e.getMessage());
            return;
        }

        // Ejecutamos deleteRelacion y verificamos
        try {
            tx.begin();
            cut.deleteRelacion(idProducto, idTipoProducto);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            fail("Error al eliminar relación: " + e.getMessage());
            return;
        }

        Producto eliminado = em.find(Producto.class, idProducto);
        assertNull(eliminado);
    }
    */

}

