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
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ComboDetalle;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ComboDetallePK;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComboDetalleBeanIT {

    EntityManagerFactory emf;

    static Network red = Network.newNetwork();

    @Container
    static GenericContainer postgres = new PostgreSQLContainer<>("postgres:16-alpine")
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
    public void testFindByPk() {
        System.out.println("ComboDetalleBeanIT.testFindByPk");
        ComboDetalleBean cut = new ComboDetalleBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        ComboDetallePK pk = new ComboDetallePK(1L, 1L);
        ComboDetalle encontrado = cut.findByPk(pk);
        assertNotNull(encontrado);
        assertEquals(pk, encontrado.getComboDetallePK());
    }
}
