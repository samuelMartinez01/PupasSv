package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Orden;
/**
 *
 * @author samuel
 */


@Stateless
@LocalBean
public class OrdenBean extends AbstractDataAccess<Orden> implements Serializable {
    
    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public OrdenBean() {
        super(Orden.class);
    }

    @Override
    public EntityManager getEntityManager() {
     return em;
    }
    
   
    
}
