package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoPrecio;

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
     * Busca una lista de productos asociados a un tipo de producto específico
     *
     * @param id el ID del tipo de producto.
     * @param first el índice inicial de los resultados (para paginación).
     * @param max la cantidad máxima de resultados a devolver.
     * @return una lista de productos que pertenecen al tipo de producto indicado, o {@code null} si ocurre un error.
     */
    public List<Producto> findByIdTipoProducto(Integer id, Integer first, Integer max) {
        if (id != null) {
            if (first == null || first < 0 && max == null || max < 0) {
                first = 0;
            }
            if (max == null || max <= 0 || max > 50) {
                max = 50;
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
        } else {
            Logger.getLogger(getClass().getName());
            return null;
        }
    }

    /**
     * Elimina la relación entre un producto y un tipo de producto. Si es la única relación,
     * el producto también será eliminado.
     *
     * @param idProducto el ID del producto.
     * @param idTipoProducto el ID del tipo de producto.
     */
    public void deleteRelacionTipoProducto(Long idProducto, Integer idTipoProducto) {
        if (idProducto == null || idProducto <= 0) {
            throw new IllegalArgumentException("Id invalido");
        }
        if (idTipoProducto == null || idTipoProducto <= 0) {
            throw new IllegalArgumentException("id tipo invalido");
        }
        try {
            int detalleBorrado = em.createNamedQuery("ProductoDetalle.deleteRelacionTipoProducto")
                    .setParameter("idProducto", idProducto)
                    .setParameter("idTipoProducto", idTipoProducto)
                    .executeUpdate();
            if (detalleBorrado == 1) {
                delete(idProducto);
                return;
            }
            throw new EntityNotFoundException("No se pudo eliminar la relación");
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }
    }

    /**
     * Elimina todas las relaciones de un producto con sus precios y con los detalles de orden relacionados.
     *
     * @param idProducto el ID del producto a desvincular.
     */
    public void deleteRelacionPrecio(Long idProducto) {
        if (idProducto != null && idProducto > 0) {
            try {
                // Elimina relaciones con detalles de orden
                em.createNamedQuery("OrdenDetalle.deleteByProductoPrecioProducto")
                        .setParameter("idProducto", idProducto)
                        .executeUpdate();

                // Elimina la relación con los precios del producto
                em.createNamedQuery("ProductoPrecio.deleteRelacionPrecio")
                        .setParameter("idProducto", idProducto)
                        .executeUpdate();
            } catch (PersistenceException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                        "Error al eliminar relación de precio", e);
                throw new PersistenceException("Error al eliminar relación de precio", e);
            }
        } else {
            throw new IllegalArgumentException("Id de producto inválido");
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

    public Long countByIdTipoProducto (Integer id, Integer first, Integer max) {
        try {
            return em.createNamedQuery("Producto.countByIdTipoProducto", Long.class)
                    .setParameter("idTipoProducto", id)
                    .getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return 0L;
    }

}
