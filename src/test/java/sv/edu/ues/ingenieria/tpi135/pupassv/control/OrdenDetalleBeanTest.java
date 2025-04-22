package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.OrdenDetalle;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.OrdenDetallePK;

import static org.junit.jupiter.api.Assertions.*;

public class OrdenDetalleBeanTest {

    @Test
    void testEliminarProductoDeOrden() {
        System.out.println("OrdenDetalleBeanTest.testEliminarProductoDeOrden");

        EntityManager emMock = Mockito.mock(EntityManager.class);
        Query queryMock = Mockito.mock(Query.class);
        OrdenDetalleBean cut = new OrdenDetalleBean();
        cut.em = emMock;

        OrdenDetallePK pkValido = new OrdenDetallePK(1L, 2L);
        Mockito.when(emMock.createNamedQuery("OrdenDetalle.deleteProductoDeOrden")).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("idOrden", 1L)).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("idProductoPrecio", 2L)).thenReturn(queryMock);
        Mockito.when(queryMock.executeUpdate()).thenReturn(1);

        cut.eliminarProductoDeOrden(pkValido);

        Mockito.verify(emMock).createNamedQuery("OrdenDetalle.deleteProductoDeOrden");
        Mockito.verify(queryMock).setParameter("idOrden", 1L);
        Mockito.verify(queryMock).setParameter("idProductoPrecio", 2L);
        Mockito.verify(queryMock).executeUpdate();
    }

    @Test
    void testEliminarProductoDeOrden_pkNull() {
        OrdenDetalleBean cut = new OrdenDetalleBean();
        assertThrows(IllegalArgumentException.class, () -> {
            cut.eliminarProductoDeOrden(null);
        });
    }

    @Test
    void testEliminarProductoDeOrden_EntityNotFoundException() {
        EntityManager emMock = Mockito.mock(EntityManager.class);
        Query queryMock = Mockito.mock(Query.class);
        OrdenDetalleBean cut = new OrdenDetalleBean();
        cut.em = emMock;

        OrdenDetallePK pk = new OrdenDetallePK(1L, 2L);
        Mockito.when(emMock.createNamedQuery("OrdenDetalle.deleteProductoDeOrden")).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("idOrden", 1L)).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("idProductoPrecio", 2L)).thenReturn(queryMock);
        Mockito.when(queryMock.executeUpdate()).thenThrow(new EntityNotFoundException("No se encontró"));

        assertThrows(EntityNotFoundException.class, () -> {
            cut.eliminarProductoDeOrden(pk);
        });
    }

    @Test
    void testEliminarProductoDeOrden_PersistenceException() {
        EntityManager emMock = Mockito.mock(EntityManager.class);
        Query queryMock = Mockito.mock(Query.class);
        OrdenDetalleBean cut = new OrdenDetalleBean();
        cut.em = emMock;

        OrdenDetallePK pk = new OrdenDetallePK(1L, 2L);
        Mockito.when(emMock.createNamedQuery("OrdenDetalle.deleteProductoDeOrden")).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("idOrden", 1L)).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("idProductoPrecio", 2L)).thenReturn(queryMock);
        Mockito.when(queryMock.executeUpdate()).thenThrow(new PersistenceException("Error en base de datos"));

        assertThrows(PersistenceException.class, () -> {
            cut.eliminarProductoDeOrden(pk);
        });
    }
    @Test
    void testGetEntityManager() {
        EntityManager emMock = Mockito.mock(EntityManager.class);
        OrdenDetalleBean cut = new OrdenDetalleBean();
        cut.em = emMock;

        EntityManager result = cut.getEntityManager();
        assertNotNull(result);
        assertEquals(emMock, result);
    }
    @Test
    void testEliminarRelacionOrdenDetalle() {
        EntityManager emMock = Mockito.mock(EntityManager.class);
        Query queryMock = Mockito.mock(Query.class);
        OrdenDetalleBean cut = new OrdenDetalleBean();
        cut.em = emMock;

        Long idOrden = 1L;

        Mockito.when(emMock.createNamedQuery("OrdenDetalle.deleteByOrdenId")).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("idOrden", idOrden)).thenReturn(queryMock);
        Mockito.when(queryMock.executeUpdate()).thenReturn(1);

        cut.eliminarRelacionOrdenDetalle(idOrden);

        Mockito.verify(emMock).createNamedQuery("OrdenDetalle.deleteByOrdenId");
        Mockito.verify(queryMock).setParameter("idOrden", idOrden);
        Mockito.verify(queryMock).executeUpdate();
    }
    @Test
    void testFindByPk() {
        EntityManager emMock = Mockito.mock(EntityManager.class);
        OrdenDetalleBean cut = new OrdenDetalleBean();
        cut.em = emMock;

        OrdenDetallePK pk = new OrdenDetallePK(1L, 2L);
        OrdenDetalle expected = new OrdenDetalle();  // objeto simulado
        Mockito.when(emMock.find(OrdenDetalle.class, pk)).thenReturn(expected);

        OrdenDetalle result = cut.findByPk(pk);

        assertNotNull(result);
        assertEquals(expected, result);
        Mockito.verify(emMock).find(OrdenDetalle.class, pk);
    }

}
