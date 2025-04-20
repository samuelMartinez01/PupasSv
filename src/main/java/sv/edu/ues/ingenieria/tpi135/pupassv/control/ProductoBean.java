package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class ProductoBean extends AbstractDataAccess<Producto> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public ProductoBean() {
        super(Producto.class);
    }
    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     *
     * @param id
     * @param first
     * @param max
     * @return
     */
    public List<Producto> findByIdTipoProducto(Integer id, Integer first, Integer max) {
        // Validar parámetros de entrada
        if (id == null) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                    "ID de tipo producto es nulo. Retornando lista vacía");
            return null;
        }

        if (first == null || first < 0) {
            first = 0; // Valor por defecto si es nulo o negativo
        }

        if (max == null || max <= 0 || max > 50) {
            max = 50; // Valor por defecto si es nulo, <=0 o >50
        }

        try {
            return em.createNamedQuery("Producto.findByIdTipoProducto", Producto.class)
                    .setParameter("idTipoProducto", id)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                    "Error al buscar productos por tipo: " + e.getMessage(), e);
            return null;
        }
    }

    public Integer countByIdTipoProducto (Integer id, Integer first, Integer max) {
        try {
            return em.createNamedQuery("Producto.countByIdTipoProducto", Integer.class)
                    .setParameter("idTipoProducto", id)
                    .getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return 0;
    }

    public void deleteRelacion(Long idProducto, Integer idTipoProducto) {
        if (idProducto == null || idProducto <= 0) {
            throw new IllegalArgumentException("Id invalido");
        }
        if (idTipoProducto == null || idTipoProducto <= 0) {
            throw new IllegalArgumentException("id tipo invalido");
        }
        try {
            int detalleBorrado = em.createNamedQuery("ProductoDetalle.deleteRelacion")
                    .setParameter("idProducto", idProducto)
                    .setParameter("idTipoProducto", idTipoProducto)
                    .executeUpdate();
            if (detalleBorrado == 1) {
                delete(idProducto);
                return;
            }
            throw new EntityNotFoundException("dNo se pudo eliminar la relacion");
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public void setEntityManager(EntityManager em) {
    }

}
