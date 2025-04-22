package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ComboDetalle;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ComboDetallePK;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 *
 * @author wolf
 */
public class ComboDetalleBeanTest {

    @Mock
    private EntityManager em;

    @Mock
    private Query mockQuery;

    @InjectMocks
    private ComboDetalleBean bean;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Inyección manual del EntityManager usando reflexión
        Field field = ComboDetalleBean.class.getDeclaredField("em");
        field.setAccessible(true);
        field.set(bean, em);
    }

    @Test
    void getEntityManager() {
        assertNotNull(bean.getEntityManager());
    }

    @Test
    void findByPk() {
        System.out.println("ComboDetalleBeanTest.findByPk");
        ComboDetallePK pk = new ComboDetallePK(1L, 2L);
        ComboDetalle expected = new ComboDetalle();

        when(em.find(ComboDetalle.class, pk)).thenReturn(expected);

        ComboDetalle result = bean.findByPk(pk);
        assertNotNull(result);
        assertEquals(expected, result);
        verify(em).find(ComboDetalle.class, pk);
    }

    @Test
    void deleteRelacionCombo() {
        System.out.println("ComboDetalleBeanTest.deleteRelacionCombo");
        Long idCombo = 1L;

        when(em.createNamedQuery("ComboDetalle.deleteRelacionCombo")).thenReturn(mockQuery);
        when(mockQuery.setParameter("idCombo", idCombo)).thenReturn(mockQuery);
        when(mockQuery.executeUpdate()).thenReturn(1);

        bean.deleteRelacionCombo(idCombo);

        verify(em).createNamedQuery("ComboDetalle.deleteRelacionCombo");
        verify(mockQuery).setParameter("idCombo", idCombo);
        verify(mockQuery).executeUpdate();
    }

    @Test
    void deleteProductoCombo() {
        System.out.println("ComboDetalleBeanTest.deleteProductoCombo");
        Long idCombo = 1L;
        Long idProducto = 2L;

        when(em.createNamedQuery("ComboDetalle.deleteProductoCombo")).thenReturn(mockQuery);
        when(mockQuery.setParameter("idCombo", idCombo)).thenReturn(mockQuery);
        when(mockQuery.setParameter("idProducto", idProducto)).thenReturn(mockQuery);
        when(mockQuery.executeUpdate()).thenReturn(1);

        bean.deleteProductoCombo(idCombo, idProducto);

        verify(em).createNamedQuery("ComboDetalle.deleteProductoCombo");
        verify(mockQuery).setParameter("idCombo", idCombo);
        verify(mockQuery).setParameter("idProducto", idProducto);
        verify(mockQuery).executeUpdate();
    }
}
