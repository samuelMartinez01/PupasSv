package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.PagoDetalle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PagoDetalleBeanTest {

    @Test
    void testGetEntityManager() {
        EntityManager emMock = Mockito.mock(EntityManager.class);
        PagoDetalleBean cut = new PagoDetalleBean();
        cut.em = emMock;

        EntityManager result = cut.getEntityManager();
        assertNotNull(result);
        assertEquals(emMock, result);
    }

    @Test
    void testFindDetallesByPagoId() {
        EntityManager emMock = Mockito.mock(EntityManager.class);
        TypedQuery<PagoDetalle> typedQueryMock = Mockito.mock(TypedQuery.class);
        PagoDetalleBean cut = new PagoDetalleBean();
        cut.em = emMock;

        Long idPago = 1L;
        List<PagoDetalle> expectedList = List.of(new PagoDetalle());

        Mockito.when(emMock.createNamedQuery("PagoDetalle.findByPagoId", PagoDetalle.class)).thenReturn(typedQueryMock);
        Mockito.when(typedQueryMock.setParameter("idPago", idPago)).thenReturn(typedQueryMock);
        Mockito.when(typedQueryMock.getResultList()).thenReturn(expectedList);

        List<PagoDetalle> result = cut.findDetallesByPagoId(idPago);

        assertNotNull(result);
        assertEquals(expectedList, result);

        Mockito.verify(emMock).createNamedQuery("PagoDetalle.findByPagoId", PagoDetalle.class);
        Mockito.verify(typedQueryMock).setParameter("idPago", idPago);
        Mockito.verify(typedQueryMock).getResultList();
    }
}
