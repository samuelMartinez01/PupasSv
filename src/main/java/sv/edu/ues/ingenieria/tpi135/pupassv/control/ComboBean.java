package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Combo;

import java.io.Serializable;

@LocalBean
@Stateless
public class ComboBean extends AbstractDataAccess<Combo>  implements Serializable {
    @PersistenceContext(name="PupaSv-PU")
    EntityManager em;

    public ComboBean() {
        super(Combo.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
