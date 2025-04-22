package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductoBeanTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery<Producto> mockQuery;
    @InjectMocks
    ProductoBean bean;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        Field field = ProductoBean.class.getDeclaredField("em");
        field.setAccessible(true);
        field.set(bean, em);
    }

    @Test
    void getEntityManager() {
        assertNotNull(bean.getEntityManager());
    }

    @Test
    void findByIdTipoProducto_valoresValidos() {
        System.out.println("ProductoBeanTest.findByIdTipoProducto_valoresValidos");
        Integer id = 1;
        Integer first = 0;
        Integer max = 10;
        Producto producto = new Producto();
        List<Producto> mockResult = Arrays.asList(producto);
        when(em.createNamedQuery("Producto.findByIdTipoProducto", Producto.class)).thenReturn(mockQuery);
        when(mockQuery.setParameter("idTipoProducto", id)).thenReturn(mockQuery);
        when(mockQuery.setFirstResult(first)).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(max)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResult);

        List<Producto> result = bean.findByIdTipoProducto(id, first, max);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findByIdTipoProducto_idNull() {
        System.out.println("ProductoBeanTest.findByIdTipoProducto_idNull");
        List<Producto> result = bean.findByIdTipoProducto(null, 0, 10);
        assertNull(result);
    }

    @Test
    void findByIdTipoProducto_excepcion() {
        System.out.println("ProductoBeanTest.findByIdTipoProducto_excepcion");
        Integer id = 1;
        when(em.createNamedQuery("Producto.findByIdTipoProducto", Producto.class)).thenThrow(RuntimeException.class);
        List<Producto> result = bean.findByIdTipoProducto(id, 0, 10);
        assertNull(result);
    }

    @Test
    void deleteRelacion_valoresValidos() {
        System.out.println("ProductoBeanTest.deleteRelacion_valoresValidos");
        Long idProducto = 1L;
        Integer idTipoProducto = 2;
        when(em.createNamedQuery("ProductoDetalle.deleteRelacion")).thenReturn(mockQuery);
        when(mockQuery.setParameter("idProducto", idProducto)).thenReturn(mockQuery);
        when(mockQuery.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockQuery);
        when(mockQuery.executeUpdate()).thenReturn(1);
        doNothing().when(em).remove(any());
    }


    @Test
    void deleteRelacion_idInvalido() {
        System.out.println("ProductoBeanTest.deleteRelacion_idInvalido");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> bean.deleteRelacion(null, 1));
        assertEquals("Id invalido", thrown.getMessage());
    }

    @Test
    void deleteRelacion_idTipoInvalido() {
        System.out.println("ProductoBeanTest.deleteRelacion_idTipoInvalido");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> bean.deleteRelacion(1L, null));
        assertEquals("id tipo invalido", thrown.getMessage());
    }

    @Test
    void deleteRelacion_detalleNoEncontrado() {
        System.out.println("productoBeanTest.deleteRelacion_detalleNoEncontrado");
        Long idProducto = 1L;
        Integer idTipoProducto = 2;
        when(em.createNamedQuery("ProductoDetalle.deleteRelacion")).thenReturn(mockQuery);
        when(mockQuery.setParameter("idProducto", idProducto)).thenReturn(mockQuery);
        when(mockQuery.setParameter("idTipoProducto", idTipoProducto)).thenReturn(mockQuery);
        when(mockQuery.executeUpdate()).thenReturn(0);
        assertThrows(EntityNotFoundException.class,
                () -> bean.deleteRelacion(idProducto, idTipoProducto));
    }

    @Test
    void deleteRelacion_errorDeBaseDeDatos() {
        System.out.println("ProductoBeanTest.deleteRelacion_errorDeBaseDeDatos");
        Long idProducto = 1L;
        Integer idTipoProducto = 2;

        when(em.createNamedQuery("ProductoDetalle.deleteRelacion")).thenThrow(PersistenceException.class);

        assertThrows(PersistenceException.class,
                () -> bean.deleteRelacion(idProducto, idTipoProducto));
    }
    @Test
    void getEm() {
        assertEquals(em, bean.getEm());
    }

    @Test
    void setEm() {
        EntityManager nuevoEm = mock(EntityManager.class);
        bean.setEm(nuevoEm);
        assertEquals(nuevoEm, bean.getEm());
    }

    @Test
    void setEntityManager_noHaceNada() {
        EntityManager otroEm = mock(EntityManager.class);
        bean.setEntityManager(otroEm);
        assertEquals(em, bean.getEm());
    }
}
