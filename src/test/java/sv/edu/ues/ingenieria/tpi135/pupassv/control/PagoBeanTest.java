package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Pago;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PagoBeanTest {

    @Test
    void testGetEntityManager() {
        EntityManager emMock = Mockito.mock(EntityManager.class);
        PagoBean cut = new PagoBean();
        cut.em = emMock;

        EntityManager result = cut.getEntityManager();
        assertNotNull(result);
        assertEquals(emMock, result);
    }

    @Test
    void testEliminarRelacionOrdenPago() {
        EntityManager emMock = Mockito.mock(EntityManager.class);
        Query queryMock = Mockito.mock(Query.class);
        PagoBean cut = new PagoBean();
        cut.em = emMock;

        Long idOrden = 1L;

        Mockito.when(emMock.createNamedQuery("Pago.deleteByOrdenId")).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("idOrden", idOrden)).thenReturn(queryMock);
        Mockito.when(queryMock.executeUpdate()).thenReturn(1);

        cut.eliminarRelacionOrdenPago(idOrden);

        Mockito.verify(emMock).createNamedQuery("Pago.deleteByOrdenId");
        Mockito.verify(queryMock).setParameter("idOrden", idOrden);
        Mockito.verify(queryMock).executeUpdate();
    }

    @Test
    void testFindPagosByOrdenId() {
        EntityManager emMock = Mockito.mock(EntityManager.class);
        TypedQuery<Pago> typedQueryMock = Mockito.mock(TypedQuery.class);
        PagoBean cut = new PagoBean();
        cut.em = emMock;

        Long idOrden = 1L;
        List<Pago> expectedList = List.of(new Pago());

        Mockito.when(emMock.createNamedQuery("Pago.findByOrdenId", Pago.class)).thenReturn(typedQueryMock);
        Mockito.when(typedQueryMock.setParameter("idOrden", idOrden)).thenReturn(typedQueryMock);
        Mockito.when(typedQueryMock.getResultList()).thenReturn(expectedList);

        List<Pago> result = cut.findPagosByOrdenId(idOrden);

        assertNotNull(result);
        assertEquals(expectedList, result);

        Mockito.verify(emMock).createNamedQuery("Pago.findByOrdenId", Pago.class);
        Mockito.verify(typedQueryMock).setParameter("idOrden", idOrden);
        Mockito.verify(typedQueryMock).getResultList();
    }
}
