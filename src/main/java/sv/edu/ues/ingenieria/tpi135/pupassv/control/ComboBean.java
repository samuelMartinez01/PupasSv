package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ComboDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ProductoComboDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Combo;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Componente EJB que gestiona las operaciones de persistencia relacionadas con la entidad {@link Combo}.
 * Proporciona métodos para obtener productos asociados a un combo y convertir un combo a un DTO.
 *
 * @author Samuel
 */
@Stateless
@LocalBean
public class ComboBean extends AbstractDataAccess<Combo> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    public EntityManager em;

    public ComboBean() {
        super(Combo.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Obtiene la lista de productos que forman parte de un combo, según su ID
     *
     * @param idCombo ID del combo a consultar
     * @return lista de {@link ProductoComboDTO} que representan los productos del combo
     */
    public List<ProductoComboDTO> findProductosByComboId(Long idCombo) {
        List<Object[]> productos = em.createNamedQuery("Combo.findProductosByComboId")
                .setParameter("idCombo", idCombo)
                .getResultList();

        return productos.stream()
                .map(p -> {
                    Producto producto = (Producto) p[0];
                    Integer cantidad = (Integer) p[1];
                    BigDecimal precioSugerido = (BigDecimal) p[2];
                    ProductoComboDTO dto = new ProductoComboDTO(producto, cantidad);
                    dto.setPrecioUnitario(precioSugerido);
                    dto.setPrecioTotal(precioSugerido.multiply(new BigDecimal(cantidad)));
                    return dto;
                }).collect(Collectors.toList());
    }


    /**
     * Convierte una entidad {@link Combo} a un objeto de transferencia de datos ({@link ComboDTO}),
     * incluyendo los productos asociados al combo y el precio total.
     *
     * @param combo la entidad {@link Combo} a convertir
     * @return una instancia de {@link ComboDTO} con la información estructurada del combo o null en caso de que el combo sea null
     */
    public ComboDTO convertirAComboDTO(Combo combo) {
        if (combo == null) return null;

        ComboDTO dto = new ComboDTO();
        dto.setIdCombo(combo.getIdCombo());
        dto.setNombre(combo.getNombre());
        dto.setActivo(combo.getActivo());
        dto.setDescripcionPublica(combo.getDescripcionPublica());
        List<ProductoComboDTO> productos = findProductosByComboId(combo.getIdCombo());
        dto.setProductos(productos);
        BigDecimal precioTotal = productos.stream()
                .map(ProductoComboDTO::getPrecioTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setPrecioTotal(precioTotal);
        return dto;
    }
}