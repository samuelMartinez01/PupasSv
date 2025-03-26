package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
 import java.nio.file.Paths;
import java.util.Date;
                import java.util.HashMap;
                import static org.junit.jupiter.api.Assertions.assertEquals;
                import static org.junit.jupiter.api.Assertions.assertNotNull;
        import org.junit.jupiter.api.BeforeAll;
                import org.junit.jupiter.api.MethodOrderer;
                import org.junit.jupiter.api.Order;
                import org.junit.jupiter.api.Test;
                import org.junit.jupiter.api.TestInstance;
                import org.junit.jupiter.api.TestMethodOrder;
                import org.testcontainers.containers.GenericContainer;
        import org.testcontainers.containers.Network;
                import org.testcontainers.containers.PostgreSQLContainer;
        import org.testcontainers.containers.wait.strategy.Wait;
                import org.testcontainers.junit.jupiter.Container;
                import org.testcontainers.junit.jupiter.Testcontainers;
        import org.testcontainers.utility.MountableFile;
                import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Orden;

        /**
         *
         * @author samuel
         */

        @Testcontainers
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        public class OrdenBeanIT {
            public OrdenBeanIT() {

            }

            EntityManagerFactory emf;
            
            static Network red = Network.newNetwork();

            @Container
            static GenericContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
                    .withDatabaseName("PupasBd_tpi2025")
                    .withPassword("abc123")
                    .withUsername("postgres")
                    .withInitScript("pupas_ddl.sql")
                    .withExposedPorts(5432)
                    .withNetworkAliases("db");
            
        @BeforeAll
        public void init() {
            System.out.println("Su puerto es:  " + postgres.getMappedPort(5432));
            HashMap<String, Object> propiedades = new HashMap<>();
            propiedades.put("jakarta.persistence.jdbc.url", 
                String.format("jdbc:postgresql://localhost:%d/PupasBd_tpi2025", 
                postgres.getMappedPort(5432))
            );
            emf = Persistence.createEntityManagerFactory("PupaTest", propiedades);
        }

            @Test
            @Order(1)
            public void testCount() {
                System.out.println("contar");
                OrdenBean cut = new OrdenBean();
                EntityManager em = emf.createEntityManager();
                cut.em = em;
                Long esperado = 3l;
                Long resultado = cut.count();
                assertEquals(esperado, resultado);
            }

            @Test
            @Order(2)
            public void testInsert() {
                System.out.println("Insertar");
                Long esperado = 4l;
                OrdenBean cut = new OrdenBean();
                EntityManager em = emf.createEntityManager();
                cut.em = em;
                Orden nuevo = new Orden();
                nuevo.setAnulada(false);
                nuevo.setFecha(new Date());
                nuevo.setSucursal("RAMBL");
                EntityTransaction tx = cut.em.getTransaction();
                try {
                    tx.begin();
                    cut.create(nuevo);
                    tx.commit();
                } catch (Exception ex) {
                    tx.rollback();
                }
                assertNotNull(nuevo.getIdOrden());
                assertEquals(esperado, cut.count());
            }





        }