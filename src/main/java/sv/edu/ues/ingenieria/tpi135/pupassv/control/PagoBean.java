package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Pago;
import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class PagoBean extends AbstractDataAccess<Pago> implements Serializable {
    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public PagoBean() {
        super(Pago.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Elimina la relacion entre Orden y pago
     * @param idOrden
     */
    public void eliminarRelacionOrdenPago(Long idOrden) {
        em.createNamedQuery("Pago.deleteByOrdenId")
                .setParameter("idOrden", idOrden)
                .executeUpdate();
    }

    /**
     * Busca los pagos asociados a una orden por medio del id de la orden
     * @param idOrden
     * @return
     */
    public List<Pago> findPagosByOrdenId(Long idOrden) {
        return em.createNamedQuery("Pago.findByOrdenId", Pago.class)
                .setParameter("idOrden", idOrden)
                .getResultList();
    }

}
