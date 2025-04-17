package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ProductoPrecioDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoPrecio;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class ProductoPrecioBean extends  AbstractDataAccess<ProductoPrecio> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;
    public ProductoPrecioBean() {
        super(ProductoPrecio.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Metodo para buscar el ProductoPrecio asociado a un Producto
     * @param idProducto a buscar
     * @return ProductoPrecio Entity
     */
    public ProductoPrecio findProductoPrecioByProducto(Long idProducto) {
        List<ProductoPrecio> resultados = em
                .createNamedQuery("ProductoPrecio.findProductoPrecioByIdProducto", ProductoPrecio.class)
                .setParameter("idProducto", idProducto)
                .setMaxResults(1)
                .getResultList();
        return resultados.isEmpty() ? null : resultados.get(0); // Devuelve el primer elemento o null
    }

    /**
     * Convierte una entidad ProductoPrecio a un DTO
     * @param entity Entidad a convertir
     * @return ProductoPrecioDTO
     */
    public ProductoPrecioDTO convertirADTO(ProductoPrecio entity) {
        if (entity == null) {
            return null;
        }
        ProductoPrecioDTO dto = new ProductoPrecioDTO();
        dto.setIdProductoPrecio(entity.getIdProductoPrecio());
        if (entity.getIdProducto() != null) {
            dto.setIdProducto(entity.getIdProducto().getIdProducto());
            dto.setNombreProducto(entity.getIdProducto().getNombre());
        }
        dto.setFechaDesde(entity.getFechaDesde());
        dto.setFechaHasta(entity.getFechaHasta());
        dto.setPrecioSugerido(entity.getPrecioSugerido());
        return dto;
    }
}
