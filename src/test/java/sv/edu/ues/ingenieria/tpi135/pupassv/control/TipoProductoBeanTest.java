package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.TipoProducto;
/**
 *
 * @author wolf
 */

public class TipoProductoBeanTest {

    protected List<TipoProducto> findResult;

    public TipoProductoBeanTest() {
        findResult = Arrays.asList(new TipoProducto[]{new TipoProducto(1), new TipoProducto(2), new TipoProducto(3)});
    }

    @Test
    void create() {
        System.out.println("TipoProductoBeanTest create");
        EntityManager mockEM = Mockito.mock(EntityManager.class);
        TipoProducto nuevo = new TipoProducto();
        TipoProductoBean cut = new TipoProductoBean();
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
        System.out.println("TipoProductoBeanTest.findById");

        EntityManager mock = Mockito.mock(EntityManager.class);
        TipoProductoBean cut = new TipoProductoBean();
        cut.em = mock;

        final Long idExistente = 1L;
        TipoProducto esperadoExistente = new TipoProducto(Math.toIntExact(idExistente));
        Mockito.when(mock.find(TipoProducto.class, idExistente)).thenReturn(esperadoExistente);

        TipoProducto resultadoExistente = cut.findById(idExistente);
        assertNotNull(resultadoExistente);
        assertEquals(esperadoExistente, resultadoExistente);

        final Long idNoExistente = 999L;
        Mockito.when(mock.find(TipoProducto.class, idNoExistente)).thenReturn(null);

        TipoProducto resultadoNoExistente = cut.findById(idNoExistente);
        assertNull(resultadoNoExistente);

        cut.em = null;
        assertThrows(IllegalStateException.class, () -> {
            cut.findById(idExistente);
        });

        TipoProducto resultadoIdNulo = cut.findById(null);
        assertNull(resultadoIdNulo);
    }

    @Test
    void findRange() {
        System.out.println("TipoProductoBeanTest.findRange");
        int first = 0;
        int max = 1000;
        TipoProductoBean cut = new TipoProductoBean();
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
        CriteriaQuery<TipoProducto> cqMock = Mockito.mock(CriteriaQuery.class);
        Root rootMock = Mockito.mock(Root.class);
        Mockito.when(cqMock.from(TipoProducto.class)).thenReturn(rootMock);
        EntityManager emMock = Mockito.mock(EntityManager.class);
        TypedQuery tqMock = Mockito.mock(TypedQuery.class);
        Mockito.when(tqMock.getResultList()).thenReturn(findResult);
        Mockito.when(emMock.createQuery(cqMock)).thenReturn(tqMock);
        Mockito.when(cbMock.createQuery(TipoProducto.class)).thenReturn(cqMock);
        Mockito.when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        cut.em = emMock;
        List<TipoProducto> encontrados = cut.findRange(first, max);
        assertNotNull(encontrados);
        assertEquals(findResult.size(), encontrados.size());
    }

    @Test
    void delete() {
        System.out.println("TipoProductoBeanTest.delete");
        TipoProductoBean cut = new TipoProductoBean();
        EntityManager emMock = Mockito.mock(EntityManager.class);
        cut.em = emMock;

        CriteriaBuilder cbMock = Mockito.mock(CriteriaBuilder.class);
        CriteriaDelete<TipoProducto> cdMock = Mockito.mock(CriteriaDelete.class);
        Root<TipoProducto> rootMock = Mockito.mock(Root.class);

        // ID nulo => IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            cut.delete(null);
        });

        // EM nulo => IllegalStateException
        cut.em = null;
        assertThrows(IllegalStateException.class, () -> {
            cut.delete(1);
        });

        cut.em = emMock;

        TipoProducto tipoMock = new TipoProducto();
        tipoMock.setIdTipoProducto(1);

        Mockito.when(emMock.find(TipoProducto.class, 1)).thenReturn(tipoMock);
        Mockito.when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        Mockito.when(cbMock.createCriteriaDelete(TipoProducto.class)).thenReturn(cdMock);
        Mockito.when(cdMock.from(TipoProducto.class)).thenReturn(rootMock);

        Predicate predicateMock = Mockito.mock(Predicate.class);
        Mockito.when(cbMock.equal(rootMock, tipoMock)).thenReturn(predicateMock);
        Mockito.when(cdMock.where(predicateMock)).thenReturn(cdMock);

        Query queryMock = Mockito.mock(Query.class);
        Mockito.when(emMock.createQuery(cdMock)).thenReturn(queryMock);
        Mockito.when(queryMock.executeUpdate()).thenReturn(1);

        cut.delete(1); // Sin excepción

        Mockito.verify(emMock, Mockito.times(1)).createQuery(cdMock);
        Mockito.verify(queryMock, Mockito.times(1)).executeUpdate();

        // EM.find devuelve null => EntityNotFoundException
        Mockito.when(emMock.find(TipoProducto.class, 2)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> {
            cut.delete(2);
        });

        // Fallo en executeUpdate => PersistenceException
        Mockito.when(emMock.find(TipoProducto.class, 3)).thenReturn(tipoMock);
        Mockito.when(queryMock.executeUpdate()).thenThrow(new PersistenceException());
        assertThrows(PersistenceException.class, () -> {
            cut.delete(3);
        });
    }

    @Test
    void update() {
        System.out.println("TipoProductoBeanTest.update");
        TipoProductoBean cut = new TipoProductoBean();
        TipoProducto modificado = new TipoProducto(1);
        assertThrows(IllegalArgumentException.class, () -> {
            cut.update(null);
        });
        assertThrows(IllegalStateException.class, () -> {
            cut.update(modificado);
        });
        EntityManager emMock = Mockito.mock(EntityManager.class);
        Mockito.when(emMock.merge(modificado)).thenReturn(modificado);
        cut.em = emMock;
        TipoProducto resultado = cut.update(modificado);
        assertNotNull(resultado);
        assertEquals(modificado, resultado);
    }

    @Test
    void count() {
        System.out.println("TipoProductoBeanTest.count");
        TipoProductoBean cut = new TipoProductoBean();
        assertThrows(IllegalStateException.class, () -> {
            cut.count();
        });
        EntityManager emMock = Mockito.mock(EntityManager.class);
        CriteriaBuilder cbMock = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Long> cqMock = Mockito.mock(CriteriaQuery.class);
        Root rootMock = Mockito.mock(Root.class);
        Expression exMock = Mockito.mock(Expression.class);
        Mockito.when(cqMock.from(TipoProducto.class)).thenReturn(rootMock);
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
