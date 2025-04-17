package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Pago;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.PagoDetalle;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class PagoDetalleBean extends AbstractDataAccess<PagoDetalle> implements Serializable {
    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public PagoDetalleBean() {
        super(PagoDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Busca los detalles de un pago por medio de sus IDs
     * @param idPago
     * @return lista de pagos detalles
     */
    public List<PagoDetalle> findDetallesByPagoId(Long idPago) {
        return em.createNamedQuery("PagoDetalle.findByPagoId", PagoDetalle.class)
                .setParameter("idPago", idPago)
                .getResultList();
    }

}
