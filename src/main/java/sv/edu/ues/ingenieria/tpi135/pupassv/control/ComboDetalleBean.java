package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ComboDetalle;

import java.io.Serializable;

@Stateless
@LocalBean
public class ComboDetalleBean extends AbstractDataAccess<ComboDetalle> implements Serializable {
    @PersistenceContext(name="PupaSv-PU")
    EntityManager em;

    public ComboDetalleBean() {
        super(ComboDetalle.class);

    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
