package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Orden;

/**
 *
 * @author samuel
 */
public class OrdenBeanTest {
    
    protected List<Orden> findResult;

    public OrdenBeanTest() {
        findResult = Arrays.asList(new Orden[]{new Orden(1l), new Orden(2l), new Orden(3l)});
    }
    /**
     * Test of getEntityManager method, of class OrdenBean.
     */
    
    @Test
    void create() {
        System.out.println("OrdenBeanTest create");
        EntityManager mockEM = Mockito.mock(EntityManager.class);
        Orden nuevo = new Orden();
        OrdenBean cut = new OrdenBean();
        assertThrows(IllegalArgumentException.class, () -> {
            cut.create(null);
        });
        assertThrows(IllegalStateException.class, () -> {
            cut.create(nuevo);
        });
        cut.em = mockEM;
        cut.create(nuevo);
    }
    
    @Test
    void findById() {
        System.out.println("OrdenBeanTest.findById");
        final Integer idEsperado = 1;
        Orden esperado = new Orden(idEsperado.longValue());
        OrdenBean cut = new OrdenBean();
        assertThrows(IllegalStateException.class, () -> {
            cut.findById(idEsperado);
        });
        EntityManager mock = Mockito.mock(EntityManager.class);
        Mockito.when(mock.find(Orden.class, idEsperado)).thenReturn(esperado);
        cut.em = mock;
        Orden resultado = cut.findById(idEsperado);
        assertNotNull(resultado);
        assertEquals(esperado, resultado);
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findById(null);
        });
    }
    
    @Test
    void findRange() {
        System.out.println("OrdenBeanTest.findRange");
        int first = 0;
        int max = 1000;
        OrdenBean cut = new OrdenBean();
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findRange(-1, 10);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findRange(10, -1);
        });
        assertThrows(IllegalStateException.class, () -> {
            cut.findRange(first, max);
        });
        CriteriaBuilder cbMock = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Orden> cqMock = Mockito.mock(CriteriaQuery.class);
        Root rootMock = Mockito.mock(Root.class);
        Mockito.when(cqMock.from(Orden.class)).thenReturn(rootMock);
        EntityManager emMock = Mockito.mock(EntityManager.class);
        TypedQuery tqMock = Mockito.mock(TypedQuery.class);
        Mockito.when(tqMock.getResultList()).thenReturn(findResult);
        Mockito.when(emMock.createQuery(cqMock)).thenReturn(tqMock);
        Mockito.when(cbMock.createQuery(Orden.class)).thenReturn(cqMock);
        Mockito.when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        cut.em = emMock;
        List<Orden> encontrados = cut.findRange(first, max);
        assertNotNull(encontrados);
        assertEquals(findResult.size(), encontrados.size());
    }
    
     @Test
    void delete() {
        System.out.println("OrdenBeanTest.delete");
        OrdenBean cut = new OrdenBean();
        Orden eliminado = new Orden(1l);
        assertThrows(IllegalArgumentException.class, () -> {
            cut.delete(null);
        });
        EntityManager emMock = Mockito.mock(EntityManager.class);
        assertThrows(IllegalStateException.class, () -> {
            cut.delete(eliminado);
        });
        Mockito.when(emMock.contains(eliminado)).thenReturn(true);
        cut.em = emMock;
        cut.delete(eliminado);
        Mockito.verify(emMock, Mockito.times(1)).remove(eliminado);
        Mockito.when(emMock.contains(eliminado)).thenReturn(false);
        Mockito.when(emMock.merge(eliminado)).thenReturn(eliminado);
        cut.em = emMock;
        cut.delete(eliminado);
        Mockito.verify(emMock, Mockito.times(2)).remove(eliminado);
    }

    @Test
    void update() {
        System.out.println("OrdenBeanTest.update");
        OrdenBean cut = new OrdenBean();
        Orden modificado = new Orden(1l);
        assertThrows(IllegalArgumentException.class, () -> {
            cut.update(null);
        });
        assertThrows(IllegalStateException.class, () -> {
            cut.update(modificado);
        });
        EntityManager emMock = Mockito.mock(EntityManager.class);
        Mockito.when(emMock.merge(modificado)).thenReturn(modificado);
        cut.em = emMock;
        Orden resultado = cut.update(modificado);
        assertNotNull(resultado);
        assertEquals(modificado, resultado);
    }

    @Test
    void count() {
        System.out.println("OrdenBeanTest.count");
        OrdenBean cut = new OrdenBean();
        assertThrows(IllegalStateException.class, () -> {
            cut.count();
        });
        EntityManager emMock = Mockito.mock(EntityManager.class);
        CriteriaBuilder cbMock = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Long> cqMock = Mockito.mock(CriteriaQuery.class);
        Root rootMock = Mockito.mock(Root.class);
        Expression exMock = Mockito.mock(Expression.class);
        Mockito.when(cqMock.from(Orden.class)).thenReturn(rootMock);
        Mockito.when(cbMock.count(rootMock)).thenReturn(exMock);
        Mockito.when(cbMock.createQuery(Long.class)).thenReturn(cqMock);
        TypedQuery tqMock = Mockito.mock(TypedQuery.class);
        Mockito.when(tqMock.getSingleResult()).thenReturn(2L);
        Mockito.when(emMock.createQuery(cqMock)).thenReturn(tqMock);
        Mockito.when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        cut.em = emMock;
        cut.count();
    }
}
