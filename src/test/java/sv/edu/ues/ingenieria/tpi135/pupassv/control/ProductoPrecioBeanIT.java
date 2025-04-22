package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoPrecio;
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
public class ProductoPrecioBeanIT {

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
    public void testFindByIdProducto() {
        System.out.println("ProductoPrecioBeanIT.testFindByIdProducto");
        ProductoPrecioBean cut = new ProductoPrecioBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        List<ProductoPrecio> lista = cut.findByIdProducto(1, 0, 10);
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    @Order(2)
    public void testFindCurrentPrice() {
        System.out.println("ProductoPrecioBeanIT.testFindCurrentPrice");
        ProductoPrecioBean cut = new ProductoPrecioBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        ProductoPrecio precio = cut.findPrice(1);
        assertNotNull(precio);
        assertEquals(1, precio.getIdProducto().getIdProducto());
    }

}
