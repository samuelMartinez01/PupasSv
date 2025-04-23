package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoDetalle;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoDetallePK;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.TipoProducto;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
//chepe
@Stateless
@LocalBean
public class ProductoDetalleBean extends AbstractDataAccess<ProductoDetalle> implements Serializable {
    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public ProductoDetalleBean() {
        super(ProductoDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Obtienen el nombre del tipoProducto al que pertenece el Producto
     * @param idProducto
     * @return Entidad Producto_detalle
     */
    public TipoProducto findTipoProducto(Long idProducto) {
        try {
            return em.createNamedQuery("ProductoDetalle.findTipoProducto", TipoProducto.class)
                    .setParameter("idProducto", idProducto)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error buscando tipoProducto", e);
            return null;
        }
    }

    public void deletePk(ProductoDetallePK pk) {
        ProductoDetalle entity = em.find(ProductoDetalle.class, pk);
        if (entity != null) {
            em.remove(entity);
        }
    }
}
