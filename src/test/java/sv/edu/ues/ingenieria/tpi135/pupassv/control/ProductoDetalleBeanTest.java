package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoDetalle;
/**
 *
 * @author wolf
 */

public class ProductoDetalleBeanTest {

    protected List<ProductoDetalle> findResult;

    public ProductoDetalleBeanTest() {
        findResult = Arrays.asList(new ProductoDetalle[]{new ProductoDetalle(1L), new ProductoDetalle(2L), new ProductoDetalle(3L)});
    }

    @Test
    void create() {
        System.out.println("ProductoDetalleBeanTest.create");
        EntityManager mockEM = Mockito.mock(EntityManager.class);
        ProductoDetalle nuevo = new ProductoDetalle(1L);
        ProductoDetalleBean cut = new ProductoDetalleBean();
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
        System.out.println("ProductoDetalleBeanTest.findById");

        EntityManager mock = Mockito.mock(EntityManager.class);
        ProductoDetalleBean cut = new ProductoDetalleBean();
        cut.em = mock;

        final Long idExistente = 1L;
        ProductoDetalle esperadoExistente = new ProductoDetalle(idExistente);
        Mockito.when(mock.find(ProductoDetalle.class, idExistente)).thenReturn(esperadoExistente);

        ProductoDetalle resultadoExistente = cut.findById(idExistente);
        assertNotNull(resultadoExistente);
        assertEquals(esperadoExistente, resultadoExistente);

        final Long idNoExistente = 999L;
        Mockito.when(mock.find(ProductoDetalle.class, idNoExistente)).thenReturn(null);

        ProductoDetalle resultadoNoExistente = cut.findById(idNoExistente);
        assertNull(resultadoNoExistente);

        cut.em = null;
        assertThrows(IllegalStateException.class, () -> {
            cut.findById(idExistente);
        });

        ProductoDetalle resultadoIdNulo = cut.findById(null);
        assertNull(resultadoIdNulo);
    }

    @Test
    void findRange() {
        System.out.println("ProductoDetalleBeanTest.findRange");
        int first = 0;
        int max = 1000;
        ProductoDetalleBean cut = new ProductoDetalleBean();
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
        CriteriaQuery<ProductoDetalle> cqMock = Mockito.mock(CriteriaQuery.class);
        Root rootMock = Mockito.mock(Root.class);
        Mockito.when(cqMock.from(ProductoDetalle.class)).thenReturn(rootMock);
        EntityManager emMock = Mockito.mock(EntityManager.class);
        TypedQuery tqMock = Mockito.mock(TypedQuery.class);
        Mockito.when(tqMock.getResultList()).thenReturn(findResult);
        Mockito.when(emMock.createQuery(cqMock)).thenReturn(tqMock);
        Mockito.when(cbMock.createQuery(ProductoDetalle.class)).thenReturn(cqMock);
        Mockito.when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        cut.em = emMock;

        List<ProductoDetalle> encontrados = cut.findRange(first, max);
        assertNotNull(encontrados);
        assertEquals(findResult.size(), encontrados.size());
    }

    @Test
    void delete() {
        System.out.println("ProductoDetalleBeanTest.delete");
        ProductoDetalleBean cut = new ProductoDetalleBean();
        EntityManager emMock = Mockito.mock(EntityManager.class);
        cut.em = emMock;

        CriteriaBuilder cbMock = Mockito.mock(CriteriaBuilder.class);
        CriteriaDelete<ProductoDetalle> cdMock = Mockito.mock(CriteriaDelete.class);
        Root<ProductoDetalle> rootMock = Mockito.mock(Root.class);

        // ID nulo => IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            cut.delete(null);
        });

        // EM nulo => IllegalStateException
        cut.em = null;
        assertThrows(IllegalStateException.class, () -> {
            cut.delete(1L);
        });
        cut.em = emMock;
        ProductoDetalle detalleMock = new ProductoDetalle(1L);
        Mockito.when(emMock.find(ProductoDetalle.class, 1L)).thenReturn(detalleMock);
        Mockito.when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        Mockito.when(cbMock.createCriteriaDelete(ProductoDetalle.class)).thenReturn(cdMock);
        Mockito.when(cdMock.from(ProductoDetalle.class)).thenReturn(rootMock);
        Predicate predicateMock = Mockito.mock(Predicate.class);
        Mockito.when(cbMock.equal(rootMock.get("idProducto"), 1L)).thenReturn(predicateMock);
        Mockito.when(cdMock.where(predicateMock)).thenReturn(cdMock);
        Query queryMock = Mockito.mock(Query.class);
        Mockito.when(emMock.createQuery(cdMock)).thenReturn(queryMock);
        Mockito.when(queryMock.executeUpdate()).thenReturn(1);
        cut.delete(1L);
        Mockito.verify(emMock, Mockito.times(1)).createQuery(cdMock);
        Mockito.verify(queryMock, Mockito.times(1)).executeUpdate();
        Mockito.when(emMock.find(ProductoDetalle.class, 2L)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> {
            cut.delete(2L);
        });
        Mockito.when(emMock.find(ProductoDetalle.class, 3L)).thenReturn(detalleMock);
        Mockito.when(queryMock.executeUpdate()).thenThrow(new PersistenceException());
        assertThrows(PersistenceException.class, () -> {
            cut.delete(3L);
        });
    }


    @Test
    void update() {
        System.out.println("ProductoDetalleBeanTest.update");
        ProductoDetalleBean cut = new ProductoDetalleBean();
        ProductoDetalle modificado = new ProductoDetalle(1L);
        assertThrows(IllegalArgumentException.class, () -> {
            cut.update(null);
        });
        assertThrows(IllegalStateException.class, () -> {
            cut.update(modificado);
        });

        EntityManager emMock = Mockito.mock(EntityManager.class);
        Mockito.when(emMock.merge(modificado)).thenReturn(modificado);
        cut.em = emMock;
        ProductoDetalle resultado = cut.update(modificado);
        assertNotNull(resultado);
        assertEquals(modificado, resultado);
    }

    @Test
    void count() {
        System.out.println("ProductoDetalleBeanTest.count");
        ProductoDetalleBean cut = new ProductoDetalleBean();
        assertThrows(IllegalStateException.class, () -> {
            cut.count();
        });

        EntityManager emMock = Mockito.mock(EntityManager.class);
        CriteriaBuilder cbMock = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Long> cqMock = Mockito.mock(CriteriaQuery.class);
        Root rootMock = Mockito.mock(Root.class);
        Expression exMock = Mockito.mock(Expression.class);

        Mockito.when(cqMock.from(ProductoDetalle.class)).thenReturn(rootMock);
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
