package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.List;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ComboDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ProductoComboDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Combo;
/**
 *
 * @author wolf
 */

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComboBeanIT {

    public ComboBeanIT() {
    }

    EntityManagerFactory emf;
    //EntityManager em;
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
                        postgres.getMappedPort(5432))
        );
        emf = Persistence.createEntityManagerFactory("PupaTest", propiedades);
    }
    @Test
    @Order(0)
    public void testFindAll_coverage() {
        System.out.println("ComboBeanIt.testFindAll_coverage");
        ComboBean cut = new ComboBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
    }


    @Test
    @Order(1)
    public void testFindProductosByComboId() {
        System.out.println("ComboBeanIT.findProductosByComboId");
        ComboBean cut = new ComboBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Long idCombo = 1L;
        List<ProductoComboDTO> resultado = cut.findProductosByComboId(idCombo);
    }

    @Test
    @Order(2)
    public void testConvertirAComboDTO() {
        System.out.println("ComboBeanIT.convertirAComboDTO");
        ComboBean cut = new ComboBean();
        EntityManager em = emf.createEntityManager();
        cut.em = em;
        Combo combo = em.find(Combo.class, 1L);
        assertNotNull(combo, "Combo con ID 1 no existe en la base de datos");
        ComboDTO dto = cut.convertirAComboDTO(combo);
        assertNotNull(dto);
        assertEquals(combo.getIdCombo(), dto.getIdCombo());
        assertEquals(combo.getNombre(), dto.getNombre());
        assertNotNull(dto.getProductos());
        assertFalse(dto.getProductos().isEmpty(), "El DTO debe contener productos");
        assertTrue(dto.getPrecioTotal().compareTo(BigDecimal.ZERO) > 0, "El precio total debe ser mayor que cero");
    }
}