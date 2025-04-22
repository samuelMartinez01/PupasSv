package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
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
                System.out.println("OrdenBeanIT.testCount");
                OrdenBean cut = new OrdenBean();
                EntityManager em = emf.createEntityManager();
                cut.em = em;
                Long esperado = 4l;
                Long resultado = cut.count();
                assertEquals(esperado, resultado);
            }

            @Test
            @Order(2)
            public void testInsert() {
                System.out.println("OrdenBeanIT.testInsert");
                Long esperado = 5l;
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
            @Test
            @Order(3)
            public void testFindById() {
                System.out.println("OrdenBeanIT.testFindById");
                OrdenBean cut = new OrdenBean();
                EntityManager em = emf.createEntityManager();
                cut.em = em;

                Orden resultado = cut.findById(1L);
                assertNotNull(resultado);
                assertEquals(1L, resultado.getIdOrden());
            }

            @Test
            @Order(4)
            public void testFindRange() {
                System.out.println("OrdenBeanIT.testFindRange");
                OrdenBean cut = new OrdenBean();
                EntityManager em = emf.createEntityManager();
                cut.em = em;

                List<Orden> resultados = cut.findRange(0, 2);
                assertNotNull(resultados);
                assertEquals(2, resultados.size());
            }

            @Test
            @Order(5)
            public void testUpdate() {
                System.out.println("OrdenBeanIT.testUpdate");
                OrdenBean cut = new OrdenBean();
                EntityManager em = emf.createEntityManager();
                cut.em = em;

                EntityTransaction tx = em.getTransaction();
                Orden orden = cut.findById(1L);
                orden.setSucursal("NUEVA");

                try {
                    tx.begin();
                    Orden actualizado = cut.update(orden);
                    tx.commit();
                    assertNotNull(actualizado);
                    assertEquals("NUEVA", actualizado.getSucursal());
                } catch (Exception e) {
                    tx.rollback();
                    throw e;
                }
            }

            @Test
            @Order(6)
            public void testDelete() {
                System.out.println("OrdenBeanIT.testDelete");
                OrdenBean cut = new OrdenBean();
                EntityManager em = emf.createEntityManager();
                cut.em = em;

                EntityTransaction tx = em.getTransaction();
                Orden orden = new Orden();
                orden.setAnulada(false);
                orden.setFecha(new Date());
                orden.setSucursal("ELIM");

                try {
                    tx.begin();
                    cut.create(orden);
                    tx.commit();
                } catch (Exception ex) {
                    tx.rollback();
                }

                Long idAEliminar = orden.getIdOrden();
                assertNotNull(idAEliminar);

                try {
                    tx.begin();
                    cut.delete(idAEliminar);
                    tx.commit();
                } catch (Exception ex) {
                    tx.rollback();
                    throw ex;
                }

                Orden eliminado = cut.findById(idAEliminar);
                assertEquals(null, eliminado);
            }

        }
