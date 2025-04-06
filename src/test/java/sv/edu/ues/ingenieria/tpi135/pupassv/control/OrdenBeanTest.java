package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;

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

        // Configuración del mock
        EntityManager mock = Mockito.mock(EntityManager.class);
        OrdenBean cut = new OrdenBean();
        cut.em = mock;

        // 1. Test para ID existente
        final Long idExistente = 1L;
        Orden esperadoExistente = new Orden(idExistente);
        Mockito.when(mock.find(Orden.class, idExistente)).thenReturn(esperadoExistente);

        Orden resultadoExistente = cut.findById(idExistente);
        assertNotNull(resultadoExistente);
        assertEquals(esperadoExistente, resultadoExistente);

        // 2. Test para ID que no existe
        final Long idNoExistente = 999L;
        Mockito.when(mock.find(Orden.class, idNoExistente)).thenReturn(null);

        Orden resultadoNoExistente = cut.findById(idNoExistente);
        assertNull(resultadoNoExistente);

        // 3. Test para EntityManager no disponible
        cut.em = null;
        assertThrows(IllegalStateException.class, () -> {
            cut.findById(idExistente);
        });

        // 4. Test para ID nulo
        Orden resultadoIdNulo = cut.findById(null);
        assertNull(resultadoIdNulo);
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

        // 1. Configurar el mock del EntityManager
        EntityManager emMock = Mockito.mock(EntityManager.class);
        cut.em = emMock;

        // 2. Mockear los componentes de Criteria API
        CriteriaBuilder cbMock = Mockito.mock(CriteriaBuilder.class);
        CriteriaDelete<Orden> cdMock = Mockito.mock(CriteriaDelete.class);
        Root<Orden> rootMock = Mockito.mock(Root.class);

        Mockito.when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        Mockito.when(cbMock.createCriteriaDelete(Orden.class)).thenReturn(cdMock);
        Mockito.when(cdMock.from(Orden.class)).thenReturn(rootMock);

        // 3. Prueba con ID nulo o inválido
        assertThrows(IllegalArgumentException.class, () -> {
            cut.delete(null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            cut.delete(0L); // ID <= 0
        });

        // 4. Prueba cuando EntityManager no está disponible
        cut.em = null;
        assertThrows(IllegalStateException.class, () -> {
            cut.delete(1L);
        });
        cut.em = emMock; // Restaurar el mock

        // 5. Mockear la entidad a eliminar
        Orden ordenMock = new Orden(1L);
        Mockito.when(emMock.find(Orden.class, 1L)).thenReturn(ordenMock);

        // 6. Mockear la condición WHERE
        Predicate predicateMock = Mockito.mock(Predicate.class);
        Mockito.when(cbMock.equal(rootMock, ordenMock)).thenReturn(predicateMock);
        Mockito.when(cdMock.where(predicateMock)).thenReturn(cdMock);

        // 7. Mockear la ejecución de la consulta
        Query queryMock = Mockito.mock(Query.class);
        Mockito.when(emMock.createQuery(cdMock)).thenReturn(queryMock);
        Mockito.when(queryMock.executeUpdate()).thenReturn(1); // 1 fila afectada

        // 8. Ejecutar el delete exitoso
        cut.delete(1L);

        // 9. Verificar que se ejecutó la consulta
        Mockito.verify(emMock, Mockito.times(1)).createQuery(cdMock);
        Mockito.verify(queryMock, Mockito.times(1)).executeUpdate();

        // 10. Prueba cuando la entidad no existe
        Mockito.when(emMock.find(Orden.class, 2L)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> {
            cut.delete(2L);
        });

        // 11. Prueba error en persistencia
        Mockito.when(queryMock.executeUpdate()).thenThrow(new PersistenceException());
        assertThrows(PersistenceException.class, () -> {
            cut.delete(1L);
        });
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
