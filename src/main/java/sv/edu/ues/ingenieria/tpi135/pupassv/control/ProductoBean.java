package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoDetalle;

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
    public List<Producto> findByIdTipoProducto (Integer id, Integer first, Integer max) {
        try {
            return em.createNamedQuery("Producto.findByIdTipoProducto", Producto.class)
                    .setParameter("idTipoProducto", id)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return List.of();
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

    public void crearProducto(Producto producto, Integer idTipoProducto) {
        if (idTipoProducto == null || producto == null) {
            throw new NullPointerException("El id o el producto no puede ser nulo");
        } else {
            try {
                em.persist(producto);
                em.flush();
                em.refresh(producto);

                ProductoDetalle productoDetalle = new ProductoDetalle(idTipoProducto, producto.getIdProducto());
                productoDetalle.setActivo(true);
                em.persist(productoDetalle);
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                throw new IllegalStateException("Error al crear el producto");
            }
        }
    }

}
