package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.*;
import java.io.Serializable;

@Stateless
@LocalBean
public class OrdenDetalleBean extends AbstractDataAccess<OrdenDetalle> implements Serializable {
    @Inject
    ProductoPrecioBean ppBean;
@Inject
OrdenBean oBean;
        @PersistenceContext(unitName = "PupaSV-PU")
        EntityManager em;

        public OrdenDetalleBean() {
            super(OrdenDetalle.class);
        }

        @Override
        public EntityManager getEntityManager() {
            return em;
        }

    /**
     * Elimina todos los detalles asociados a una orden específica.
     *
     * @param idOrden el ID de la orden que se eliminaron sus detalles
     */
    public void eliminarRelacionOrdenDetalle(Long idOrden) {
        em.createNamedQuery("OrdenDetalle.deleteByOrdenId")
                .setParameter("idOrden", idOrden)
                .executeUpdate();
    }

    /**
     * Busca un detalle de orden usando su clave primaria compuesta.
     *
     * @param pk la clave primaria compuesta del detalle de la orden.
     * @return el objeto {@link OrdenDetalle} correspondiente, o {@code null} si no se encuentra.
     */
    public OrdenDetalle findByPk(OrdenDetallePK pk) {
        return em.find(OrdenDetalle.class, pk);
    }


    /**
     * Elimina un producto específico de una orden, usando la clave primaria compuesta.
     *
     * @param pk la clave primaria que identifica el detalle del producto en la orden.
     */
    public void eliminarProductoDeOrden(OrdenDetallePK pk) {
        if (pk != null) {
            try {
                em.createNamedQuery("OrdenDetalle.deleteProductoDeOrden")
                        .setParameter("idOrden", pk.getIdOrden())
                        .setParameter("idProductoPrecio", pk.getIdProductoPrecio())
                        .executeUpdate();
            } catch (EntityNotFoundException e) {
                throw e;
            } catch (PersistenceException e) {
                throw new PersistenceException(e);
            }
        } else {
            throw new IllegalArgumentException("Clave primaria no válida");
        }
    }

}
