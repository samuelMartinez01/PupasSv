package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;

import java.io.Serializable;

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
}
