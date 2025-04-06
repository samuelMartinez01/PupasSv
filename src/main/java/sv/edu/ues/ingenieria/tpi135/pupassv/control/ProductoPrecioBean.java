package sv.edu.ues.ingenieria.tpi135.pupassv.control;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    public ProductoPrecio findPrice(Integer idProducto) {
        return em.createNamedQuery("ProductoPrecio.findCurrentByProducto", ProductoPrecio.class)
                .setParameter("idProducto", idProducto)
                .getSingleResult();
    }

    public List<ProductoPrecio> findByIdProducto(Integer idProducto, int first, int max) {
        return em.createNamedQuery("ProductoPrecio.findByIdProducto", ProductoPrecio.class)
                .setParameter("idProducto", idProducto)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }
}
