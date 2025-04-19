package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoPrecio;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductoPrecioBeanTest {
    @Mock
    private EntityManager em;
    @InjectMocks
    private ProductoPrecioBean productoPrecioBean;
    @Mock
    private TypedQuery<ProductoPrecio> query;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findPrice_Success() {
        Integer idProducto = 1;
        ProductoPrecio mockProductoPrecio = mock(ProductoPrecio.class);
        when(em.createNamedQuery("ProductoPrecio.findCurrentByProducto", ProductoPrecio.class)).thenReturn(query);
        when(query.setParameter("idProducto", idProducto)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(mockProductoPrecio);
        ProductoPrecio result = productoPrecioBean.findPrice(idProducto);
        assertNotNull(result);
        assertEquals(mockProductoPrecio, result);
        verify(query).setParameter("idProducto", idProducto);
        verify(query).getSingleResult();
    }

    @Test
    public void findPrice_NoResult() {
        // Arrange
        Integer idProducto = 1;

        when(em.createNamedQuery("ProductoPrecio.findCurrentByProducto", ProductoPrecio.class)).thenReturn(query);
        when(query.setParameter("idProducto", idProducto)).thenReturn(query);
        when(query.getSingleResult()).thenThrow(NoResultException.class);

        // Act & Assert
        assertThrows(NoResultException.class, () -> productoPrecioBean.findPrice(idProducto));
        verify(query).setParameter("idProducto", idProducto);
        verify(query).getSingleResult();
    }

    @Test
    public void findByIdProducto_Success() {
        Integer idProducto = 1;
        List<ProductoPrecio> mockList = Arrays.asList(mock(ProductoPrecio.class), mock(ProductoPrecio.class));
        when(em.createNamedQuery("ProductoPrecio.findByIdProducto", ProductoPrecio.class)).thenReturn(query);
        when(query.setParameter("idProducto", idProducto)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(mockList);
        List<ProductoPrecio> result = productoPrecioBean.findByIdProducto(idProducto, 0, 10);
        assertNotNull(result);
        assertEquals(mockList, result);
        verify(query).setParameter("idProducto", idProducto);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    public void findByIdProducto_EmptyList() {
        Integer idProducto = 1;
        List<ProductoPrecio> mockList = Arrays.asList();
        when(em.createNamedQuery("ProductoPrecio.findByIdProducto", ProductoPrecio.class)).thenReturn(query);
        when(query.setParameter("idProducto", idProducto)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(mockList);
        List<ProductoPrecio> result = productoPrecioBean.findByIdProducto(idProducto, 0, 10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(query).setParameter("idProducto", idProducto);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    public void findByIdProducto_Exception() {
        Integer idProducto = 1;
        when(em.createNamedQuery("ProductoPrecio.findByIdProducto", ProductoPrecio.class)).thenReturn(query);
        when(query.setParameter("idProducto", idProducto)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> productoPrecioBean.findByIdProducto(idProducto, 0, 10));
        verify(query).setParameter("idProducto", idProducto);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }
    @Test
    void testGetEntityManager() throws Exception {
        // Crear un mock de EntityManager
        EntityManager emMock = mock(EntityManager.class);

        // Instanciar el bean
        ProductoPrecioBean bean = new ProductoPrecioBean();

        // Inyectar el mock en el campo privado 'em'
        Field field = ProductoPrecioBean.class.getDeclaredField("em");
        field.setAccessible(true);
        field.set(bean, emMock);

        // Ahora el método debería devolver el mock, no null
        assertNotNull(bean.getEntityManager());
    }
}
