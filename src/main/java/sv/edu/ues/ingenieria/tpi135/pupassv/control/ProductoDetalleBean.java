package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoDetalle;

@Stateless
@LocalBean
public class ProductoDetalleBean extends AbstractDataAccess<ProductoDetalle> {
    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public ProductoDetalleBean() {
        super(ProductoDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
