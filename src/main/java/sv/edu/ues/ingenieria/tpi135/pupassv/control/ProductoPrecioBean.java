package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
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

    public ProductoPrecio findPrice(Integer idProducto) {
        if (idProducto == null) {
            throw new IllegalArgumentException("ID de producto no puede ser nulo");
        }
        try {
            return em.createNamedQuery("ProductoPrecio.findCurrentByProducto", ProductoPrecio.class)
                    .setParameter("idProducto", idProducto)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException("No se encontró precio para el producto con ID: " + idProducto);
        }
    }

    public List<ProductoPrecio> findByIdProducto(Integer idProducto, int first, int max) {
        return em.createNamedQuery("ProductoPrecio.findByIdProducto", ProductoPrecio.class)
                .setParameter("idProducto", idProducto)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }
}
