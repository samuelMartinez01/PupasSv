package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.OrdenDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.OrdenDetalleDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Orden;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.OrdenDetalle;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.OrdenDetallePK;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;

/**
 *
 * @author samuel
 */
@Stateless
@LocalBean
public class OrdenBean extends AbstractDataAccess<Orden> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public OrdenBean() {
        super(Orden.class);
    }

    @Override
    public EntityManager getEntityManager() {
     return em;
    }

    /**
     * Metodo para convertir una Entity {@link Orden} a un DTO
     * @param orden
     * @return OrdenDTO
     */
    public OrdenDTO convertirAOrdenDTO(Orden orden) {
        if (orden == null) {
            return null;}
        OrdenDTO dto = new OrdenDTO();
        dto.setIdOrden(orden.getIdOrden());
        dto.setSucursal(orden.getSucursal());
        dto.setFecha(orden.getFecha());
        dto.setAnulada(orden.getAnulada());
        List<OrdenDetalleDTO> detallesDTO = convertirDetallesADTO(orden);
        dto.setDetalles(detallesDTO);
        dto.setTotal(calcularTotal(detallesDTO));
        return dto;
    }/**
     * Calcula el total para el Dto de Orden
     * @param detalles
     * @return Bigdecimal de total de la orden
     */
    private BigDecimal calcularTotal(List<OrdenDetalleDTO> detalles) {
        return detalles.stream()
                .map(detalle -> {
                    if (detalle.getPrecioUnitario() != null) {
                        return detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                    }return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    /**
     * Convirte los detalles (lista) de una ORden (Entity) a Detalles DTO
     * @param orden
     * @return
     */
    private List<OrdenDetalleDTO> convertirDetallesADTO(Orden orden) {
        return findDetallesByIdOrden(orden.getIdOrden()).stream()
                .map(this::convertirDetalleADTO)
                .collect(Collectors.toList());
    }/**
     * Convirte una OrdenDetalle (Entity) a un DetalleDTO (singular)
     * @param detalle
     * @return
     */
    private OrdenDetalleDTO convertirDetalleADTO(OrdenDetalle detalle) {
        OrdenDetalleDTO detalleDTO = new OrdenDetalleDTO();
        if (detalle.getOrdenDetallePK() != null) {
            detalleDTO.setIdProductoPrecio(detalle.getOrdenDetallePK().getIdProductoPrecio());}
        detalleDTO.setCantidad(detalle.getCantidad());
        detalleDTO.setPrecioUnitario(detalle.getPrecio());
        detalleDTO.setObservaciones(detalle.getObservaciones());
        if (detalle.getProductoPrecio() != null &&
                detalle.getProductoPrecio().getIdProducto() != null) {
            Producto producto = detalle.getProductoPrecio().getIdProducto();
            detalleDTO.setIdProducto(producto.getIdProducto());
            detalleDTO.setNombreProducto(producto.getNombre());}
        return detalleDTO;
    }
    /**
     * Busca detalle de una orden por medio del id de la orden
     * @param idOrden
     * @return
     */
    public List<OrdenDetalle> findDetallesByIdOrden(Long idOrden) {
        if (idOrden == null) {
            return Collections.emptyList();
        }try {
            return em.createNamedQuery("OrdenDetalle.findProductosByIdOrden", OrdenDetalle.class)
                    .setParameter("idOrden", idOrden)
                    .getResultList();} catch (Exception e) {
            Logger.getLogger(OrdenBean.class.getName()).log(Level.SEVERE, "Error al buscar detalles con productos", e);
            return Collections.emptyList();
        }}

    /**
     * Obtiene una referencia (LAZY) a una entidad {@link Orden}
     * sin cargar sus datos de la base de datos para ser mas eficiente
     * @param idOrden el identificador único de la orden a la que se desea obtener referencia.
     * @return una referencia (carga perezosa) a la entidad {@link Orden} con el ID especificado.
     */
    public Orden getOrdenReference(Long idOrden) {
        return em.getReference(Orden.class, idOrden);
    }
}
