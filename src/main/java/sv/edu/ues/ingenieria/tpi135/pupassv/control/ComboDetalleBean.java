package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ComboDetalle;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ComboDetallePK;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class ComboDetalleBean extends AbstractDataAccess<ComboDetalle> implements Serializable {
    @PersistenceContext(name="PupaSV-PU")
    EntityManager em;

    public ComboDetalleBean() {
        super(ComboDetalle.class);

    }
    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    /**
     * Metodo para verificar si ya existe una relación entre un combo y un producto
     * antes de crearla o actualizarla.
     * @param pk
     * @return null sino existe o true si existe
     */
    public ComboDetalle findByPk(ComboDetallePK pk) {
        return em.find(ComboDetalle.class, pk);
    }

    /**
     * Elimina una relación específica entre un combo y un producto
     * @param idCombo ID del combo
     * @return número de registros eliminados (0 o 1)
     */
    public void deleteRelacionCombo(Long idCombo) {
         em.createNamedQuery("ComboDetalle.deleteRelacionCombo")
                .setParameter("idCombo", idCombo)
                .executeUpdate();
    }

    /**
     * Metodo para eliminar un producto especifico del combo
     * @param idCombo
     * @param idProducto
     */
    public void deleteProductoCombo(Long idCombo, Long idProducto) {
        em.createNamedQuery("ComboDetalle.deleteProductoCombo")
                .setParameter("idCombo", idCombo)
                .setParameter("idProducto", idProducto)
                .executeUpdate();
    }

    public List<ComboDetalle> findByProducto(Long idProducto) {
        try {
            return em.createNamedQuery("ComboDetalle.findByProducto", ComboDetalle.class)
                    .setParameter("idProducto", idProducto)
                    .getResultList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al buscar relaciones combo", e);
            return Collections.emptyList();
        }
    }
}
