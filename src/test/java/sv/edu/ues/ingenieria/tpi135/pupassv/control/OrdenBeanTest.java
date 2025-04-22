package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.OrdenDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrdenBeanTest {

    @Test
    void testGetEntityManager() {
        EntityManager emMock = mock(EntityManager.class);
        OrdenBean cut = new OrdenBean();
        cut.em = emMock;

        assertEquals(emMock, cut.getEntityManager());
    }

    @Test
    void testGetOrdenReference() {
        EntityManager emMock = mock(EntityManager.class);
        Orden ordenMock = new Orden();
        when(emMock.getReference(Orden.class, 1L)).thenReturn(ordenMock);

        OrdenBean cut = new OrdenBean();
        cut.em = emMock;

        Orden result = cut.getOrdenReference(1L);

        assertNotNull(result);
        assertEquals(ordenMock, result);
    }
    @Test
    void testFindDetallesByIdOrden_Valid() {
        EntityManager emMock = mock(EntityManager.class);
        TypedQuery<OrdenDetalle> queryMock = mock(TypedQuery.class);
        List<OrdenDetalle> detalles = Collections.singletonList(new OrdenDetalle());

        when(emMock.createNamedQuery("OrdenDetalle.findProductosByIdOrden", OrdenDetalle.class)).thenReturn(queryMock);
        when(queryMock.setParameter("idOrden", 1L)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(detalles);

        OrdenBean cut = new OrdenBean();
        cut.em = emMock;

        List<OrdenDetalle> result = cut.findDetallesByIdOrden(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(queryMock).getResultList();
    }

    @Test
    void testFindDetallesByIdOrden_NullId() {
        OrdenBean cut = new OrdenBean();
        List<OrdenDetalle> result = cut.findDetallesByIdOrden(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindDetallesByIdOrden_Exception() {
        EntityManager emMock = mock(EntityManager.class);
        when(emMock.createNamedQuery(anyString(), eq(OrdenDetalle.class)))
                .thenThrow(RuntimeException.class);

        OrdenBean cut = new OrdenBean();
        cut.em = emMock;

        List<OrdenDetalle> result = cut.findDetallesByIdOrden(1L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertirAOrdenDTO_Completo() {
        Orden orden = new Orden();
        orden.setIdOrden(1L);
        orden.setSucursal("Sucursal A");
        orden.setFecha(new java.util.Date());
        orden.setAnulada(Boolean.FALSE);

        OrdenDetalle detalle = new OrdenDetalle();
        OrdenDetallePK pk = new OrdenDetallePK(1L, 2L);
        detalle.setOrdenDetallePK(pk);
        detalle.setCantidad(2);
        detalle.setPrecio(BigDecimal.valueOf(5));
        detalle.setObservaciones("Obs");

        Producto producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Producto A");

        ProductoPrecio productoPrecio = new ProductoPrecio();
        productoPrecio.setIdProducto(producto);
        detalle.setProductoPrecio(productoPrecio);

        EntityManager emMock = mock(EntityManager.class);
        TypedQuery<OrdenDetalle> queryMock = mock(TypedQuery.class);
        when(emMock.createNamedQuery("OrdenDetalle.findProductosByIdOrden", OrdenDetalle.class)).thenReturn(queryMock);
        when(queryMock.setParameter("idOrden", 1L)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(Collections.singletonList(detalle));

        OrdenBean cut = new OrdenBean();
        cut.em = emMock;

        OrdenDTO dto = cut.convertirAOrdenDTO(orden);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdOrden());
        assertEquals("Sucursal A", dto.getSucursal());
        assertEquals(Boolean.FALSE, dto.getAnulada());
        assertEquals(1, dto.getDetalles().size());
        assertEquals(BigDecimal.valueOf(10), dto.getTotal());
    }

    @Test
    void testConvertirAOrdenDTO_Null() {
        OrdenBean cut = new OrdenBean();
        assertNull(cut.convertirAOrdenDTO(null));
    }

}
