package sv.edu.ues.ingenieria.tpi135.pupassv.DTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Combo;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class ComboDTO {
    public ComboDTO() {}
        private Long idCombo;
        private String nombre;
        private Boolean activo;
        private String descripcionPublica;
        private List<ProductoComboDTO> productos;
        private BigDecimal precioTotal;


    public ComboDTO(Combo combo) {
        this.idCombo = combo.getIdCombo();
        this.nombre = combo.getNombre();
        this.activo = combo.getActivo();
        this.descripcionPublica = combo.getDescripcionPublica();
    }

    public Long getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(Long idCombo) {
        this.idCombo = idCombo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcionPublica() {
        return descripcionPublica;
    }

    public void setDescripcionPublica(String descripcionPublica) {
        this.descripcionPublica = descripcionPublica;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<ProductoComboDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoComboDTO> productos) {
        this.productos = productos;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }
}
